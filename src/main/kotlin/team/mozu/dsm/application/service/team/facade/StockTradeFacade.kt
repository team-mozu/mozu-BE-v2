package team.mozu.dsm.application.service.team.facade

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.`in`.team.dto.request.CompleteInvestmentRequest
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.InsufficientCashException
import team.mozu.dsm.application.exception.team.InsufficientStockQuantityException
import team.mozu.dsm.application.exception.team.StockNotOwnedException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.CommandStockPort
import team.mozu.dsm.application.port.out.team.CommandTeamPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.domain.team.model.Stock
import team.mozu.dsm.domain.team.model.Team
import team.mozu.dsm.domain.team.type.OrderType
import java.time.LocalDateTime
import java.util.UUID

/**
 * 매수, 매도 요청을 처리해 Stock을 생성·수정·삭제하고 팀의 현금과 자산을 업데이트
 */
@Component
class StockTradeFacade(
    private val queryLessonPort: QueryLessonPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryStockPort: QueryStockPort,
    private val commandStockPort: CommandStockPort,
    private val commandTeamPort: CommandTeamPort
) {
    fun execute(requests: List<CompleteInvestmentRequest>, team: Team) {
        val lesson = queryLessonPort.findByLessonNum(team.lessonNum)
            ?: throw LessonNotFoundException

        val lessonId = lesson.id ?: throw LessonNotFoundException
        val teamId = team.id ?: throw TeamNotFoundException
        val currentRound = lesson.curInvRound

        val groupedRequests = requests.groupBy { Pair(it.itemId, it.itemName) }
        val itemIds = groupedRequests.keys.map { it.first }

        val lessonItemMap = queryLessonItemPort.findAllByLessonIdAndItemIds(lessonId, itemIds)
            .associateBy { it.lessonItemId.itemId }

        val stocksToSave = mutableListOf<Stock>()
        val stockIdsToDelete = mutableListOf<UUID>()
        var currentCashMoney = team.cashMoney

        val existingStocks = queryStockPort.findAllByTeamId(teamId)
        var currentValuationMoney = existingStocks.sumOf { stock ->
            val lessonItem = lessonItemMap[stock.itemId]
            val currentPrice = if (lessonItem != null) {
                lessonItem.getPriceByRound(currentRound) ?: lessonItem.endPrice
            } else 0L
            currentPrice * stock.quantity
        }

        val totalBuyAmount = requests.filter { it.orderType == OrderType.BUY }
            .sumOf { r ->
                val price = lessonItemMap[r.itemId]?.getPriceByRound(currentRound)
                    ?: lessonItemMap[r.itemId]?.endPrice
                    ?: throw LessonItemNotFoundException
                price * r.orderCount
            }
        val totalSellAmount = requests.filter { it.orderType == OrderType.SELL }
            .sumOf { r ->
                val price = lessonItemMap[r.itemId]?.getPriceByRound(currentRound)
                    ?: lessonItemMap[r.itemId]?.endPrice
                    ?: throw LessonItemNotFoundException
                price * r.orderCount
            }

        if (currentCashMoney + totalSellAmount < totalBuyAmount) {
            throw InsufficientCashException
        }

        groupedRequests.forEach { (itemKey, itemRequests) ->
            val (itemId, itemName) = itemKey
            val currentStock = queryStockPort.findByTeamIdAndItemId(teamId, itemId)
            val lessonItem = lessonItemMap[itemId] ?: throw LessonItemNotFoundException
            val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.endPrice

            val buyRequests = itemRequests.filter { it.orderType == OrderType.BUY }
            val sellRequests = itemRequests.filter { it.orderType == OrderType.SELL }
            val totalBuyCount = buyRequests.sumOf { it.orderCount }
            val totalSellCount = sellRequests.sumOf { it.orderCount }
            val itemTotalBuyAmount = currentPrice * totalBuyCount
            val itemTotalSellAmount = currentPrice * totalSellCount
            val netQuantityChange = totalBuyCount - totalSellCount

            if (currentStock == null) {
                if (totalSellCount > 0) throw StockNotOwnedException

                if (netQuantityChange > 0) {
                    val avgPrice = if (totalBuyCount > 0) itemTotalBuyAmount / totalBuyCount else 0L
                    val valProfit = (currentPrice * netQuantityChange) - itemTotalBuyAmount
                    val profitNum = if (itemTotalBuyAmount > 0)
                        (valProfit.toDouble() / itemTotalBuyAmount.toDouble()) * 100 else 0.0

                    stocksToSave.add(
                        Stock(
                            id = null,
                            teamId = teamId,
                            itemId = itemId,
                            itemName = itemName,
                            avgPurchasePrice = avgPrice,
                            quantity = netQuantityChange,
                            buyMoney = itemTotalBuyAmount,
                            valProfit = valProfit,
                            profitNum = profitNum,
                            createdAt = LocalDateTime.now(),
                            updatedAt = null
                        )
                    )
                    currentCashMoney -= itemTotalBuyAmount
                    currentValuationMoney += currentPrice * netQuantityChange
                }
            } else {
                if (totalSellCount > currentStock.quantity) throw InsufficientStockQuantityException

                val newQuantity = currentStock.quantity + netQuantityChange

                when {
                    newQuantity > 0 -> {
                        val (newAvgPrice, newBuyMoney) = when {
                            totalBuyCount > 0 && totalSellCount == 0 -> {
                                val newTotalValue = currentStock.avgPurchasePrice * currentStock.quantity + itemTotalBuyAmount
                                val newTotalQuantity = currentStock.quantity + totalBuyCount
                                val avgPrice = if (newTotalQuantity > 0) newTotalValue / newTotalQuantity else currentStock.avgPurchasePrice
                                Pair(avgPrice, currentStock.buyMoney + itemTotalBuyAmount)
                            }
                            totalBuyCount == 0 && totalSellCount > 0 -> {
                                val sellRatio = totalSellCount.toDouble() / currentStock.quantity.toDouble()
                                val newBuyMoney = (currentStock.buyMoney * (1.0 - sellRatio)).toLong()
                                Pair(currentStock.avgPurchasePrice, newBuyMoney)
                            }
                            totalBuyCount > 0 && totalSellCount > 0 -> {
                                val sellRatio = totalSellCount.toDouble() / currentStock.quantity.toDouble()
                                val buyMoneyAfterSell = (currentStock.buyMoney * (1.0 - sellRatio)).toLong()
                                val quantityAfterSell = currentStock.quantity - totalSellCount
                                val newTotalValue = currentStock.avgPurchasePrice * quantityAfterSell + itemTotalBuyAmount
                                val newTotalQuantity = quantityAfterSell + totalBuyCount
                                val avgPrice = if (newTotalQuantity > 0) newTotalValue / newTotalQuantity else currentStock.avgPurchasePrice
                                Pair(avgPrice, buyMoneyAfterSell + itemTotalBuyAmount)
                            }
                            else -> Pair(currentStock.avgPurchasePrice, currentStock.buyMoney)
                        }

                        val valProfit = (currentPrice * newQuantity) - newBuyMoney
                        val profitNum = if (newBuyMoney > 0)
                            (valProfit.toDouble() / newBuyMoney.toDouble()) * 100 else 0.0

                        stocksToSave.add(
                            currentStock.copy(
                                quantity = newQuantity,
                                avgPurchasePrice = newAvgPrice,
                                buyMoney = newBuyMoney,
                                valProfit = valProfit,
                                profitNum = profitNum,
                                updatedAt = LocalDateTime.now()
                            )
                        )

                        currentCashMoney = currentCashMoney - itemTotalBuyAmount + itemTotalSellAmount
                        val previousValuation = currentStock.quantity * currentPrice
                        currentValuationMoney = currentValuationMoney - previousValuation + (newQuantity * currentPrice)
                    }

                    newQuantity == 0 -> {
                        currentStock.id?.let { stockIdsToDelete.add(it) }
                        currentCashMoney += itemTotalSellAmount
                        currentValuationMoney -= currentStock.quantity * currentPrice
                    }
                }
            }
        }

        if (stocksToSave.isNotEmpty()) commandStockPort.saveAll(stocksToSave)
        stockIdsToDelete.forEach { commandStockPort.deleteById(it) }

        val currentStocks = queryStockPort.findAllByTeamId(teamId)
        val stockItemIds = currentStocks.map { it.itemId }.distinct()

        val stockLessonItemMap = if (stockItemIds.isNotEmpty()) {
            queryLessonItemPort.findAllByLessonIdAndItemIds(lessonId, stockItemIds)
                .associateBy { it.lessonItemId.itemId }
        } else emptyMap()

        val valuationMoney = currentStocks.sumOf { stock ->
            val lessonItem = stockLessonItemMap[stock.itemId] ?: throw LessonItemNotFoundException
            val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.endPrice
            currentPrice * stock.quantity
        }

        commandTeamPort.update(
            team.copy(
                cashMoney = currentCashMoney,
                valuationMoney = valuationMoney,
                totalMoney = currentCashMoney + valuationMoney,
                isInvestmentInProgress = lesson.curInvRound < lesson.maxInvRound,
                updatedAt = LocalDateTime.now()
            )
        )
    }
}
