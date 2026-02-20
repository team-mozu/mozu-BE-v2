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
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.sse.PublishToSseUseCase
import team.mozu.dsm.application.port.`in`.team.CompleteTeamInvestmentUseCase
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.application.port.out.team.CommandOrderItemPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.application.service.team.facade.StockProfitFacade
import team.mozu.dsm.application.service.team.facade.StockTradeFacade
import team.mozu.dsm.domain.team.model.OrderItem
import java.time.LocalDateTime
import java.util.UUID

/**
 * 전체 투자 완료 흐름을 제어하는 서비스
 */
@Service
class CompleteTeamInvestmentService(
    private val queryTeamPort: QueryTeamPort,
    private val queryLessonPort: QueryLessonPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryOrganPort: QueryOrganPort,
    private val queryStockPort: QueryStockPort,
    private val commandOrderItemPort: CommandOrderItemPort,
    private val queryItemPort: QueryItemPort,
    private val publishToSseUseCase: PublishToSseUseCase,
    private val stockTradeFacade: StockTradeFacade,
    private val stockProfitFacade: StockProfitFacade
) : CompleteTeamInvestmentUseCase {

    @Transactional
    override fun execute(requests: List<CompleteInvestmentRequest>, lessonNum: String, teamId: UUID) {
        val lesson = queryLessonPort.findByLessonNum(lessonNum) ?: throw LessonNotFoundException
        val team = queryTeamPort.findByIdWithLock(teamId) ?: throw TeamNotFoundException
        val organ = queryOrganPort.findModelById(lesson.organId) ?: throw OrganNotFoundException
        val lessonId = lesson.id ?: throw LessonNotFoundException

        validateItems(requests.map { it.itemId }.toSet(), lessonId)

        commandOrderItemPort.saveAll(
            requests.map { req ->
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
        )

        stockProfitFacade.updateNonTradedStocksProfit(teamId, lesson, requests.map { it.itemId })

        stockTradeFacade.execute(requests, team)

        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    val updatedTeam = queryTeamPort.findById(teamId) ?: return
                    val updatedStocks = queryStockPort.findAllByTeamId(teamId)
                    val profitCalculationRound = lesson.curInvRound + 1

                    val lessonItemMap = if (updatedStocks.isNotEmpty()) {
                        queryLessonItemPort.findAllByLessonIdAndItemIds(
                            lessonId, updatedStocks.map { it.itemId }.distinct()
                        ).associateBy { it.lessonItemId.itemId }
                    } else emptyMap()

                    val stockValuationMoney = updatedStocks.sumOf { stock ->
                        val lessonItem = lessonItemMap[stock.itemId] ?: return@sumOf 0L
                        val currentPrice = lessonItem.getPriceByRound(profitCalculationRound) ?: lessonItem.endPrice
                        currentPrice * stock.quantity
                    }

                    val totalMoneyWithNextRound = updatedTeam.cashMoney + stockValuationMoney
                    val currentTotalValProfit = totalMoneyWithNextRound - lesson.baseMoney
                    val profitNum = if (lesson.baseMoney > 0)
                        (currentTotalValProfit.toDouble() / lesson.baseMoney.toDouble()) * 100 else 0.0

                    publishToSseUseCase.publishTo(
                        "lesson-organ-sse:$lessonId:${organ.id}",
                        "TEAM_INV_END",
                        TeamInvestmentCompletedEventDTO(
                            teamId = teamId,
                            teamName = updatedTeam.teamName,
                            curInvRound = lesson.curInvRound,
                            totalMoney = totalMoneyWithNextRound,
                            valuationMoney = currentTotalValProfit,
                            profitNum = profitNum
                        )
                    )
                }
            }
        )
    }

    private fun validateItems(itemIds: Set<Int>, lessonId: UUID) {
        val validItemIds = queryLessonItemPort.findItemIdsByLessonId(lessonId)
        if (itemIds.any { it !in validItemIds }) throw InvalidItemException

        val items = queryItemPort.findAllByIds(itemIds)
        val existingItemIds = items.map { it.id }.toSet()
        if (itemIds.any { it !in existingItemIds }) throw ItemNotFoundException
        if (items.any { it.isDeleted }) throw ItemDeletedException
    }
}
