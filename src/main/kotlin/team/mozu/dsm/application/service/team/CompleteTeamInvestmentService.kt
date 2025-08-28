package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import team.mozu.dsm.adapter.`in`.team.dto.TeamInvestmentCompletedEventDTO
import team.mozu.dsm.adapter.`in`.team.dto.request.CompleteInvestmentRequest
import team.mozu.dsm.application.exception.item.InvalidItemException
import team.mozu.dsm.application.exception.item.ItemDeletedException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.exception.team.InsufficientStockQuantityException
import team.mozu.dsm.application.exception.team.StockNotOwnedException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.sse.PublishToSseUseCase
import team.mozu.dsm.application.port.`in`.team.CompleteTeamInvestmentUseCase
import team.mozu.dsm.application.port.out.item.ItemQueryPort
import team.mozu.dsm.application.port.out.lesson.LessonItemQueryPort
import team.mozu.dsm.application.port.out.lesson.LessonQueryPort
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.application.port.out.team.*
import team.mozu.dsm.domain.team.model.Stock
import team.mozu.dsm.domain.team.type.OrderType
import java.time.LocalDateTime
import java.util.UUID

@Service
class CompleteTeamInvestmentService(
    private val teamQueryPort: TeamQueryPort,
    private val lessonQueryPort: LessonQueryPort,
    private val lessonItemQueryPort: LessonItemQueryPort,
    private val orderItemCommandPort: OrderItemCommandPort,
    private val stockQueryPort: StockQueryPort,
    private val queryOrganPort: QueryOrganPort,
    private val stockCommandPort: StockCommandPort,
    private val teamCommandPort: TeamCommandPort,
    private val itemQueryPort: ItemQueryPort,
    private val publishToSseUseCase: PublishToSseUseCase
) : CompleteTeamInvestmentUseCase {

    @Transactional
    override fun completeInvestment(requests: List<CompleteInvestmentRequest>, lessonNum: String, teamId: UUID) {

        val lesson = lessonQueryPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        val team = teamQueryPort.findById(teamId)
            ?: throw TeamNotFoundException

        val organ = queryOrganPort.findById(lesson.organId)
            ?: throw OrganNotFoundException

        val currentInvCount = lesson.curInvRound

        val itemIds = requests.map { it.itemId }
        val lessonId = lesson.id ?: throw LessonNotFoundException
        validateItems(itemIds, lessonId)

        orderItemCommandPort.saveAll(requests, team, currentInvCount)

        updateStocksAndTeam(requests, teamId)

        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {

                    val updatedTeam = teamQueryPort.findById(teamId)
                        ?: throw TeamNotFoundException

                    val stocks = stockQueryPort.findAllByTeamId(teamId)
                    val currentLesson = lessonQueryPort.findByLessonNum(updatedTeam.lessonNum)
                        ?: throw LessonNotFoundException

                    val currentLessonId = currentLesson.id ?: throw LessonNotFoundException
                    val currentRound = lesson.curInvRound

                    val lessonItemMap = lessonItemQueryPort.findAllByLessonIdAndItemIds(
                        currentLessonId,
                        stocks.map { it.itemId }.distinct()
                    ).associateBy { it.lessonItemId.itemId }

                    val totalBuyMoney = stocks.sumOf { it.buyMoney }
                    val totalValProfit = stocks.sumOf { stock ->
                        val lessonItem = lessonItemMap[stock.itemId]
                        val currentPrice = lessonItem?.getPriceByRound(currentRound) ?: lessonItem?.currentMoney ?: 0
                        (currentPrice * stock.quantity.toLong()) - stock.buyMoney
                    }

                    val profitNum = if (totalBuyMoney > 0) {
                        (totalValProfit.toDouble() / totalBuyMoney.toDouble()) * 100
                    } else {
                        0.0
                    }

                    val eventData = TeamInvestmentCompletedEventDTO(
                        teamId = teamId,
                        teamName = updatedTeam.teamName,
                        curInvRound = currentInvCount,
                        totalMoney = updatedTeam.totalMoney,
                        valuationMoney = updatedTeam.valuationMoney,
                        profitNum = profitNum
                    )
                    publishToSseUseCase.publishTo(organ.id.toString() , "TEAM_INV_END", eventData)
                }
            }
        )
    }

    /**
     * 거래하려는 종목이 유효한지 검증
     */
    private fun validateItems(itemIds: List<UUID>, lessonId: UUID) {
        val validItemIds = lessonItemQueryPort.findItemIdsByLessonId(lessonId)

        val invalidItems = itemIds.filter { it !in validItemIds }

        if (invalidItems.isNotEmpty()) {
            throw InvalidItemException
        }

        val deletedItems = itemIds.filter { itemId ->
            val item = itemQueryPort.findById(itemId)
            item?.isDeleted == true
        }

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
        teamId: UUID
    ) {

        // ================================================
        // 데이터 조회 및 초기화
        // ================================================
        val team = teamQueryPort.findById(teamId)
            ?: throw TeamNotFoundException

        val lesson = lessonQueryPort.findByLessonNum(team.lessonNum)
            ?: throw LessonNotFoundException

        val lessonId = lesson.id
            ?: throw LessonNotFoundException

        val groupedRequests = requests.groupBy { Pair(it.itemId, it.itemName) }

        val allItemIds = groupedRequests.keys.map { it.first }

        val lessonItemMap = lessonItemQueryPort.findAllByLessonIdAndItemIds(lessonId, allItemIds)
            .associateBy { it.lessonItemId.itemId }

        val stocksToSave = mutableListOf<Stock>()
        val stockIdsToDelete = mutableListOf<UUID>()

        // ================================================
        // 주식 처리 로직
        // ================================================
        groupedRequests.forEach { (itemKey, itemRequests) ->
            val (itemId, itemName) = itemKey
            val currentStock = stockQueryPort.findByTeamIdAndItemId(teamId, itemId)

            val buyRequests = itemRequests.filter { it.orderType == OrderType.BUY }
            val sellRequests = itemRequests.filter { it.orderType == OrderType.SELL }

            val totalBuyCount = buyRequests.sumOf { it.orderCount }
            val totalSellCount = sellRequests.sumOf { it.orderCount }
            val totalBuyAmount = buyRequests.sumOf { it.totalAmount }

            val netChange = totalBuyCount - totalSellCount
            val lessonItem = lessonItemMap[itemId]
            val nextRound = lesson.curInvRound + 1
            val nextPrice = lessonItem?.getPriceByRound(nextRound) ?: lessonItem?.currentMoney ?: 0

            // === 신규 주식 처리 ===
            if (currentStock == null) {

                if (totalSellCount > 0) {
                    throw StockNotOwnedException
                }

                if (netChange > 0) {
                    val avgPrice = if (totalBuyCount > 0) totalBuyAmount / totalBuyCount else 0L

                    /** 평가손익 = (현재가 × 보유수량) - 총매수금액
                     * 다음 차시에 맞는 수익률을 확인해야하기 때문에 현재가가 아닌 다음차시의 가격 사용
                     */
                    val newStockValProfit = (nextPrice * netChange) - totalBuyAmount

                    // 수익률 = (평가손익 ÷ 총매수금액) × 100
                    val newStockProfitNum = if (totalBuyAmount > 0) {
                        (newStockValProfit.toDouble() / totalBuyAmount.toDouble()) * 100
                    } else {
                        0.0
                    }

                    val newStock = Stock(
                        id = null,
                        teamId = teamId,
                        itemId = itemId,
                        itemName = itemName,
                        avgPurchasePrice = avgPrice, // 평균 매입가 = 거래 가격
                        quantity = netChange,
                        buyMoney = totalBuyAmount,
                        valProfit = newStockValProfit,
                        profitNum = newStockProfitNum,
                        createdAt = LocalDateTime.now(),
                        updatedAt = null
                    )
                    stocksToSave.add(newStock)
                }
            } else {
                // === 기존 주식 업데이트 ===
                if (totalSellCount > currentStock.quantity) {
                    throw InsufficientStockQuantityException
                }

                val newQuantity = currentStock.quantity + netChange

                when {
                    newQuantity > 0 -> {

                        val (newAvgPrice, newBuyMoney) = when {

                            // === 매수 주식 처리 ===
                            totalBuyCount > 0 && totalSellCount == 0 -> {
                                val currentTotalValue = currentStock.avgPurchasePrice * currentStock.quantity
                                val newTotalValue = currentTotalValue + totalBuyAmount
                                val newTotalQuantity = currentStock.quantity + totalBuyCount
                                val avgPrice =
                                    if (newTotalQuantity > 0) newTotalValue / newTotalQuantity else currentStock.avgPurchasePrice

                                Pair(avgPrice, currentStock.buyMoney + totalBuyAmount)
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
                                val newTotalValue = currentTotalValue + totalBuyAmount
                                val newTotalQuantity = quantityAfterSell + totalBuyCount
                                val avgPrice =
                                    if (newTotalQuantity > 0) newTotalValue / newTotalQuantity else currentStock.avgPurchasePrice
                                val newBuyMoney = buyMoneyAfterSell + totalBuyAmount

                                Pair(avgPrice, newBuyMoney)
                            }

                            else -> Pair(currentStock.avgPurchasePrice, currentStock.buyMoney)
                        }

                        // 평가 손익 계산
                        val valProfit = (nextPrice * newQuantity) - newBuyMoney

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
                    }

                    newQuantity == 0 -> {
                        currentStock.id?.let { stockIdsToDelete.add(it) }
                    }

                    newQuantity < 0 -> {
                        // 보유 수량보다 많이 매도하는 것은 불가능함
                        throw InsufficientStockQuantityException
                    }
                }
            }
        }

        if (stocksToSave.isNotEmpty()) {
            stockCommandPort.saveAll(stocksToSave)
        }

        stockIdsToDelete.forEach { stockId ->
            stockCommandPort.deleteById(stockId)
        }

        // ================================================
        // 팀 정보 업데이트
        // ================================================

        val cashChange = requests.sumOf { request ->
            when (request.orderType) {
                OrderType.SELL -> request.totalAmount
                OrderType.BUY -> -request.totalAmount
            }
        }
        val newCash = team.cashMoney + cashChange

        val currentStocks = stockQueryPort.findAllByTeamId(teamId)
        val stockItemIds = currentStocks.map { it.itemId }.distinct()

        val nextRound = lesson.curInvRound + 1

        // 평가액 = 보유 수량 * 주식의 현재가
        val stockLessonItemMap = if (stockItemIds.isNotEmpty()) {
            lessonItemQueryPort.findAllByLessonIdAndItemIds(lessonId, stockItemIds)
                .associateBy { it.lessonItemId.itemId }
        } else {
            emptyMap()
        }

        val totalEvaluationAmount = currentStocks.sumOf { stock ->
            val lessonItem = stockLessonItemMap[stock.itemId]
            val nextPrice = lessonItem?.getPriceByRound(nextRound) ?: lessonItem?.currentMoney ?: 0
            nextPrice * stock.quantity.toLong()
        }

        val totalMoney = newCash + totalEvaluationAmount
        val isInvestmentInProgress = lesson.curInvRound < lesson.maxInvRound

        val updatedTeam = team.copy(
            cashMoney = newCash,
            valuationMoney = totalEvaluationAmount,
            totalMoney = totalMoney,
            isInvestmentInProgress = isInvestmentInProgress,
            updatedAt = LocalDateTime.now()
        )
        teamCommandPort.save(updatedTeam)
    }
}
