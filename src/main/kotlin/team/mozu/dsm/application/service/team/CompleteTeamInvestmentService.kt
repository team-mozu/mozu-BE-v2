package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import team.mozu.dsm.adapter.`in`.team.dto.TeamInvestmentCompletedEventDTO
import team.mozu.dsm.adapter.`in`.team.dto.request.CompleteInvestmentRequest
import team.mozu.dsm.application.exception.item.InvalidItemException
import team.mozu.dsm.application.exception.item.ItemDeletedException
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.exception.team.InsufficientCashException
import team.mozu.dsm.application.exception.team.InsufficientStockQuantityException
import team.mozu.dsm.application.exception.team.StockNotOwnedException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.sse.PublishToSseUseCase
import team.mozu.dsm.application.port.`in`.team.CompleteTeamInvestmentUseCase
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.application.port.out.team.CommandOrderItemPort
import team.mozu.dsm.application.port.out.team.CommandStockPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.application.port.out.team.CommandTeamPort
import team.mozu.dsm.domain.lesson.model.Lesson
import team.mozu.dsm.domain.team.model.OrderItem
import team.mozu.dsm.domain.team.model.Stock
import team.mozu.dsm.domain.team.model.Team
import team.mozu.dsm.domain.team.type.OrderType
import java.time.LocalDateTime
import java.util.UUID

@Service
class CompleteTeamInvestmentService(
    private val queryTeamPort: QueryTeamPort,
    private val queryLessonPort: QueryLessonPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val commandOrderItemPort: CommandOrderItemPort,
    private val queryStockPort: QueryStockPort,
    private val queryOrganPort: QueryOrganPort,
    private val commandStockPort: CommandStockPort,
    private val commandTeamPort: CommandTeamPort,
    private val queryItemPort: QueryItemPort,
    private val publishToSseUseCase: PublishToSseUseCase
) : CompleteTeamInvestmentUseCase {

    @Transactional
    override fun completeInvestment(requests: List<CompleteInvestmentRequest>, lessonNum: String, teamId: UUID) {
        val lesson = queryLessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        val team = queryTeamPort.findByIdWithLock(teamId)
            ?: throw TeamNotFoundException

        val organ = queryOrganPort.findModelById(lesson.organId)
            ?: throw OrganNotFoundException

        val itemIds = requests.map { it.itemId }.toSet()
        val lessonId = lesson.id ?: throw LessonNotFoundException
        validateItems(itemIds, lessonId)

        val orderItems = requests.map { req ->
            OrderItem(
                id = null,
                itemId = req.itemId,
                teamId = team.id!!,
                itemName = req.itemName,
                orderType = req.orderType,
                orderCount = req.orderCount,
                itemPrice = req.itemPrice,
                totalMoney = req.totalMoney,
                invCount = lesson.curInvRound,
                createdAt = LocalDateTime.now(),
                updatedAt = null
            )
        }
        commandOrderItemPort.saveAll(orderItems)

        updatePreviouslyTradedStocksProfit(teamId, lesson, requests.map { it.itemId })

        updateStocksAndTeam(requests, team)

        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    //트랜잭션 시작 시점의 오래된 값을 사용할 수 있어 재조회함
                    val updatedTeam = queryTeamPort.findById(teamId) ?: return
                    val updatedStocks = queryStockPort.findAllByTeamId(teamId)

                    val currentRound = lesson.curInvRound
                    // SSE 이벤트 수익 계산을 위한 차수: 현재 진행 차수(N) + 1
                    val profitCalculationRound = lesson.curInvRound + 1

                    val stockItemIds = updatedStocks.map { it.itemId }.distinct()

                    val lessonItemMap = if (stockItemIds.isNotEmpty()) {
                        queryLessonItemPort.findAllByLessonIdAndItemIds(lessonId, stockItemIds)
                            .associateBy { it.lessonItemId.itemId }
                    } else {
                        emptyMap()
                    }

                    // 평가손익 계산 (N+1 차수 기준)
                    val stockValuationMoney = updatedStocks.sumOf { stock ->
                        val lessonItem = lessonItemMap[stock.itemId] ?: return@sumOf 0L
                        val currentPrice = lessonItem.getPriceByRound(profitCalculationRound) ?: lessonItem.currentMoney
                        currentPrice * stock.quantity
                    }

                    // 총 자산 = 현금 + 주식 평가액 (N+1 차수 기준)
                    val totalMoneyWithNextRound = updatedTeam.cashMoney + stockValuationMoney

                    // 평가손익 = 총 자산 - 초기 자산
                    val currentTotalValProfit = totalMoneyWithNextRound - lesson.baseMoney

                    val profitNum = if (lesson.baseMoney > 0) {
                        (currentTotalValProfit.toDouble() / lesson.baseMoney.toDouble()) * 100
                    } else {
                        0.0
                    }

                    val eventData = TeamInvestmentCompletedEventDTO(
                        teamId = teamId,
                        teamName = updatedTeam.teamName,
                        curInvRound = currentRound,
                        totalMoney = totalMoneyWithNextRound, // N+1 차수 기준 총 자산
                        valuationMoney = currentTotalValProfit, // N+1 차수 기준 평가손익
                        profitNum = profitNum
                    )
                    publishToSseUseCase.publishTo("lesson-organ-sse:$lessonId:${organ.id}", "TEAM_INV_END", eventData)
                }
            }
        )
    }

    /**
     * 거래하려는 종목이 유효한지 검증
     */
    private fun validateItems(itemIds: Set<Int>, lessonId: UUID) {
        val validItemIds = queryLessonItemPort.findItemIdsByLessonId(lessonId)

        val invalidItems = itemIds.filter { it !in validItemIds }

        if (invalidItems.isNotEmpty()) {
            throw InvalidItemException
        }

        val items = queryItemPort.findAllByIds(itemIds)
        val existingItemIds = items.map { it.id }.toSet()
        val notFoundItemIds = itemIds.toSet() - existingItemIds

        if (notFoundItemIds.isNotEmpty()) {
            throw ItemNotFoundException
        }

        val deletedItems = items.filter { it.isDeleted }.map { it.id }

        if (deletedItems.isNotEmpty()) {
            throw ItemDeletedException
        }
    }

    /**
     * 주식 및 팀 정보 업데이트
     * 중복 조회 방지 (team, lesson, lessonItem 등을 한 번만 조회)
     * 주식 거래는 모든 계산이 정확한 순서와 일관성 유지가 중요하기에 하나의 메서드에서 처리
     * 가독성을 위해 주석으로 분리
     */
    private fun updateStocksAndTeam(
        requests: List<CompleteInvestmentRequest>,
        team: Team
    ) {
        // ================================================
        // 데이터 조회 및 초기화
        // ================================================

        val lesson = queryLessonPort.findByLessonNum(team.lessonNum)
            ?: throw LessonNotFoundException

        val lessonId = lesson.id
            ?: throw LessonNotFoundException

        val teamId = team.id ?: throw TeamNotFoundException

        val groupedRequests = requests.groupBy { Pair(it.itemId, it.itemName) }

        val itemIds = groupedRequests.keys.map { it.first }

        val lessonItemMap = queryLessonItemPort.findAllByLessonIdAndItemIds(lessonId, itemIds)
            .associateBy { it.lessonItemId.itemId }

        val previousInv = (lesson.curInvRound - 1).coerceAtLeast(0)

        val stocksToSave = mutableListOf<Stock>()
        val stockIdsToDelete = mutableListOf<UUID>()

        // 실시간 현금 및 평가액 업데이트를 위한 변수
        var currentCashMoney = team.cashMoney

        // 현재 보유 주식들의 평가액 계산 (실시간 추적 초기값)
        val existingStocks = queryStockPort.findAllByTeamId(teamId)
        var currentValuationMoney = existingStocks.sumOf { stock ->
            val lessonItem = lessonItemMap[stock.itemId]
            val currentPrice = if (lessonItem != null) {
                lessonItem.getPriceByRound(lesson.curInvRound) ?: lessonItem.currentMoney
            } else {
                0L
            }
            currentPrice * stock.quantity
        }

        val totalBuyAmount = requests.filter { it.orderType == OrderType.BUY }
            .sumOf { r ->
                val lessonItem = lessonItemMap[r.itemId] ?: throw LessonItemNotFoundException
                val previousPrice = lessonItem.getPriceByRound(previousInv) ?: lessonItem.currentMoney
                previousPrice * r.orderCount
            }

        val totalSellAmount = requests.filter { it.orderType == OrderType.SELL }
            .sumOf { r ->
                val lessonItem = lessonItemMap[r.itemId] ?: throw LessonItemNotFoundException
                val previousPrice = lessonItem.getPriceByRound(previousInv) ?: lessonItem.currentMoney
                previousPrice * r.orderCount
            }

        if (currentCashMoney + totalSellAmount < totalBuyAmount) {
            throw InsufficientCashException
        }

        // ================================================
        // 주식 처리 로직
        // ================================================
        groupedRequests.forEach { (itemKey, itemRequests) ->
            val (itemId, itemName) = itemKey
            val currentStock = queryStockPort.findByTeamIdAndItemId(teamId, itemId)

            val buyRequests = itemRequests.filter { it.orderType == OrderType.BUY }
            val sellRequests = itemRequests.filter { it.orderType == OrderType.SELL }

            val totalBuyCount = buyRequests.sumOf { it.orderCount }
            val totalSellCount = sellRequests.sumOf { it.orderCount }
            val lessonItem = lessonItemMap[itemId] ?: throw LessonItemNotFoundException
            val currentRound = lesson.curInvRound
            val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.currentMoney
            val itemTotalBuyAmount = buyRequests
                .sumOf { r ->
                    val previousPrice = lessonItem.getPriceByRound(previousInv) ?: lessonItem.currentMoney
                    previousPrice * r.orderCount
                }
            val netQuantityChange = totalBuyCount - totalSellCount

            // === 신규 주식 처리 ===
            if (currentStock == null) {
                if (totalSellCount > 0) {
                    throw StockNotOwnedException
                }

                if (netQuantityChange > 0) {
                    val avgPrice = if (totalBuyCount > 0) itemTotalBuyAmount / totalBuyCount else 0

                    //평가손익 = (현재가 × 보유수량) - 총매수금액
                    val currentStockValProfit = (currentPrice * netQuantityChange) - itemTotalBuyAmount

                    // 수익률 = (평가손익 ÷ 총매수금액) × 100
                    val currentStockProfitNum = if (itemTotalBuyAmount > 0) {
                        (currentStockValProfit.toDouble() / itemTotalBuyAmount.toDouble()) * 100
                    } else {
                        0.0
                    }

                    val newStock = Stock(
                        id = null,
                        teamId = teamId,
                        itemId = itemId,
                        itemName = itemName,
                        avgPurchasePrice = avgPrice, // 평균 매입가 = 거래 가격
                        quantity = netQuantityChange,
                        buyMoney = itemTotalBuyAmount,
                        valProfit = currentStockValProfit,
                        profitNum = currentStockProfitNum,
                        createdAt = LocalDateTime.now(),
                        updatedAt = null
                    )
                    stocksToSave.add(newStock)

                    // 매수: 현금 차감, 평가액 즉시 업데이트
                    currentCashMoney -= itemTotalBuyAmount
                    currentValuationMoney += (currentPrice * netQuantityChange)
                }
            } else {
                // === 기존 주식 업데이트 ===
                if (totalSellCount > currentStock.quantity) {
                    throw InsufficientStockQuantityException
                }

                val newQuantity = currentStock.quantity + netQuantityChange

                when {
                    newQuantity > 0 -> {
                        val (newAvgPrice, newBuyMoney) = when {
                            // === 매수 주식 처리 ===
                            totalBuyCount > 0 && totalSellCount == 0 -> {
                                val currentTotalValue = currentStock.avgPurchasePrice * currentStock.quantity
                                val newTotalValue = currentTotalValue + itemTotalBuyAmount
                                val newTotalQuantity = currentStock.quantity + totalBuyCount
                                val avgPrice =
                                    if (newTotalQuantity > 0) newTotalValue / newTotalQuantity else currentStock.avgPurchasePrice

                                Pair(avgPrice, currentStock.buyMoney + itemTotalBuyAmount)
                            }

                            // === 매도 주식 처리 ===
                            totalBuyCount == 0 && totalSellCount > 0 -> {
                                val sellRatio = totalSellCount.toDouble() / currentStock.quantity.toDouble()
                                val newBuyMoney = (currentStock.buyMoney * (1.0 - sellRatio)).toLong()

                                Pair(currentStock.avgPurchasePrice, newBuyMoney)
                            }

                            // === 매수 & 매도 주식 처리 ===
                            totalBuyCount > 0 && totalSellCount > 0 -> {
                                val sellRatio = totalSellCount.toDouble() / currentStock.quantity.toDouble()
                                val buyMoneyAfterSell = (currentStock.buyMoney * (1.0 - sellRatio)).toLong()
                                val quantityAfterSell = currentStock.quantity - totalSellCount

                                val currentTotalValue = currentStock.avgPurchasePrice * quantityAfterSell
                                val newTotalValue = currentTotalValue + itemTotalBuyAmount
                                val newTotalQuantity = quantityAfterSell + totalBuyCount
                                val avgPrice =
                                    if (newTotalQuantity > 0) newTotalValue / newTotalQuantity else currentStock.avgPurchasePrice
                                val newBuyMoney = buyMoneyAfterSell + itemTotalBuyAmount

                                Pair(avgPrice, newBuyMoney)
                            }

                            else -> Pair(currentStock.avgPurchasePrice, currentStock.buyMoney)
                        }

                        // 평가 손익 계산
                        val valProfit = (currentPrice * newQuantity) - newBuyMoney

                        // 수익률 계산
                        val profitNum = if (newBuyMoney > 0) {
                            (valProfit.toDouble() / newBuyMoney.toDouble()) * 100
                        } else {
                            0.0
                        }

                        val updatedStock = currentStock.copy(
                            quantity = newQuantity,
                            avgPurchasePrice = newAvgPrice,
                            buyMoney = newBuyMoney,
                            valProfit = valProfit,
                            profitNum = profitNum,
                            updatedAt = LocalDateTime.now()
                        )
                        stocksToSave.add(updatedStock)

                        // 실시간 현금 및 평가액 업데이트
                        // 매수: 현금 차감, 매도: 현금 증가
                        val itemTotalSellAmount = sellRequests
                            .sumOf { r ->
                                val previousPrice = lessonItem.getPriceByRound(previousInv) ?: lessonItem.currentMoney
                                previousPrice * r.orderCount
                            }
                        currentCashMoney = currentCashMoney - itemTotalBuyAmount + itemTotalSellAmount

                        // 평가액 업데이트: 기존 평가액 제거 후 새로운 평가액 추가
                        val previousValuation = currentStock.quantity * currentPrice
                        val newValuation = newQuantity * currentPrice
                        currentValuationMoney = currentValuationMoney - previousValuation + newValuation
                    }

                    newQuantity == 0 -> {
                        currentStock.id?.let { stockIdsToDelete.add(it) }

                        // 모든 주식 매도 시 현금 증가, 평가액 제거
                        val itemTotalSellAmount = sellRequests
                            .sumOf { r ->
                                val previousPrice = lessonItem.getPriceByRound(previousInv) ?: lessonItem.currentMoney
                                previousPrice * r.orderCount
                            }
                        currentCashMoney += itemTotalSellAmount

                        // 전량 매도로 평가액에서 해당 주식 가치 제거
                        val previousValuation = currentStock.quantity * currentPrice
                        currentValuationMoney -= previousValuation
                    }
                }
            }
        }

        if (stocksToSave.isNotEmpty()) {
            commandStockPort.saveAll(stocksToSave)
        }

        stockIdsToDelete.forEach { stockId ->
            commandStockPort.deleteById(stockId)
        }

        // ================================================
        // 팀 정보 업데이트
        // ================================================

        // 실시간으로 계산된 현금 사용
        val currentCash = currentCashMoney

        val currentStocks = queryStockPort.findAllByTeamId(teamId)

        val stockItemIds = currentStocks.map { it.itemId }.distinct()

        val curInvRound = lesson.curInvRound

        val stockLessonItemMap = if (stockItemIds.isNotEmpty()) {
            queryLessonItemPort.findAllByLessonIdAndItemIds(lessonId, stockItemIds)
                .associateBy { it.lessonItemId.itemId }
        } else {
            emptyMap()
        }

        // 평가액 = 모든 보유 주식의 (수량 * 현재가) 합 (실시간 계산)
        val valuationMoney = currentStocks.sumOf { stock ->
            val lessonItem = stockLessonItemMap[stock.itemId] ?: throw LessonItemNotFoundException
            val currentPrice = lessonItem.getPriceByRound(curInvRound) ?: lessonItem.currentMoney
            currentPrice * stock.quantity
        }

        // 총 자산 = 현금 + 평가액 (항상 실시간 계산, 주문 즉시 반영)
        val totalMoney = currentCash + valuationMoney
        val isInvestmentInProgress = lesson.curInvRound < lesson.maxInvRound

        val updatedTeam = team.copy(
            cashMoney = currentCash,
            valuationMoney = valuationMoney,
            totalMoney = totalMoney,
            isInvestmentInProgress = isInvestmentInProgress,
            updatedAt = LocalDateTime.now()
        )
        commandTeamPort.update(updatedTeam)
    }

    private fun updatePreviouslyTradedStocksProfit(teamId: UUID, lesson: Lesson, tradedItemIds: List<Int>) {
        val lessonId = lesson.id ?: throw LessonNotFoundException
        val currentRound = lesson.curInvRound

        val allStocks = queryStockPort.findAllByTeamId(teamId)

        val nonTradedStocks = allStocks.filter { stock ->
            stock.itemId !in tradedItemIds
        }

        if (nonTradedStocks.isEmpty()) return

        val itemIds = nonTradedStocks.map { it.itemId }.distinct()
        val lessonItemMap = queryLessonItemPort.findAllByLessonIdAndItemIds(lessonId, itemIds)
            .associateBy { it.lessonItemId.itemId }

        val stocksToUpdate = nonTradedStocks.map { stock ->
            val lessonItem = lessonItemMap[stock.itemId] ?: throw LessonItemNotFoundException
            val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.currentMoney

            val currentValProfit = (currentPrice * stock.quantity) - stock.buyMoney
            val currentProfitNum = if (stock.buyMoney > 0) {
                (currentValProfit.toDouble() / stock.buyMoney.toDouble()) * 100
            } else {
                0.0
            }

            stock.copy(
                valProfit = currentValProfit,
                profitNum = currentProfitNum,
                updatedAt = LocalDateTime.now()
            )
        }
        commandStockPort.saveAll(stocksToUpdate)
    }
}
