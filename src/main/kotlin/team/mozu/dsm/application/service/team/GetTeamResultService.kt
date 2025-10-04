package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamResultResponse
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetTeamResultUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryOrderItemPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class GetTeamResultService(
    private val queryLessonPort: QueryLessonPort,
    private val queryTeamPort: QueryTeamPort,
    private val queryStockPort: QueryStockPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryOrderItemPort: QueryOrderItemPort
) : GetTeamResultUseCase {

    @Transactional(readOnly = true)
    override fun get(lessonNum: String, teamId: UUID): TeamResultResponse {
        val lesson = queryLessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        val team = queryTeamPort.findById(teamId)
            ?: throw TeamNotFoundException

        val stocks = queryStockPort.findAllByTeamId(teamId)
        val stockItemIds = stocks.map { it.itemId }.distinct()

        val lessonItemMap = if (stockItemIds.isNotEmpty()) {
            queryLessonItemPort.findAllByLessonIdAndItemIds(lesson.id!!, stockItemIds)
                .associateBy { it.lessonItemId.itemId }
        } else {
            emptyMap()
        }

        val totalBuyMoney = stocks.sumOf { it.buyMoney }

        val valProfit = stocks.sumOf { stock ->
            val lessonItem = lessonItemMap[stock.itemId] ?: return@sumOf 0L
            val currentPrice = lessonItem.getPriceByRound(lesson.curInvRound) ?: lessonItem.currentMoney
            (currentPrice * stock.quantity) - stock.buyMoney
        }

        val profitNum = if (totalBuyMoney > 0) {
            (valProfit.toDouble() / totalBuyMoney.toDouble()) * 100
        } else {
            0.0
        }

        val formattedProfitNum = when {
            profitNum > 0 -> "+%.2f%%".format(profitNum)
            else -> "%.2f%%".format(profitNum)
        }

        // 현재 주식 평가액 계산
        val currentStockValuation = stocks.sumOf { stock ->
            val lessonItem = lessonItemMap[stock.itemId] ?: return@sumOf 0L
            val currentPrice = lessonItem.getPriceByRound(lesson.curInvRound) ?: lessonItem.currentMoney
            currentPrice * stock.quantity
        }

        // 실제 총 자산 = 현금 + 주식 평가액
        val actualTotalMoney = team.cashMoney + currentStockValuation

        return TeamResultResponse(
            id = team.id!!,
            teamName = team.teamName,
            baseMoney = lesson.baseMoney,
            totalMoney = actualTotalMoney,
            invRound = lesson.curInvRound,
            valProfit = valProfit,
            profitNum = formattedProfitNum,
            orderCount = queryOrderItemPort.countOrderItemsByTeamId(teamId)
        )
    }
}
