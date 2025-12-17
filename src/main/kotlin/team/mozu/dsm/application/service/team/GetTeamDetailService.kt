package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamDetailResponse
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetTeamDetailUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class GetTeamDetailService(
    private val queryLessonPort: QueryLessonPort,
    private val queryTeamPort: QueryTeamPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryStockPort: QueryStockPort
) : GetTeamDetailUseCase {

    @Transactional(readOnly = true)
    override fun getTeamDetail(lessonNum: String, teamId: UUID): TeamDetailResponse {
        val lesson = queryLessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        val team = queryTeamPort.findById(teamId) ?: throw TeamNotFoundException

        val stocks = queryStockPort.findAllByTeamId(teamId)

        val currentRound = lesson.curInvRound
        val previousRound = (currentRound - 1).coerceAtLeast(1)

        val lessonItemMap = queryLessonItemPort.findAllByLessonIdAndItemIds(
            lesson.id!!,
            stocks.map { it.itemId }.distinct()
        ).associateBy { it.lessonItemId.itemId }

        // 현재 차수 기준 평가금액
        val currentStockValuation = stocks.sumOf { stock ->
            val lessonItem = lessonItemMap[stock.itemId]
                ?: throw LessonItemNotFoundException

            val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.endPrice
            currentPrice * stock.quantity
        }

        // 이전 차수 기준 평가금액
        val previousStockValuation = stocks.sumOf { stock ->
            val lessonItem = lessonItemMap[stock.itemId]
                ?: throw LessonItemNotFoundException

            val previousPrice = lessonItem.getPriceByRound(previousRound) ?: lessonItem.endPrice
            previousPrice * stock.quantity
        }

        // 실제 총 자산 = 현금 + 주식 평가액 (현재 차수 기준)
        val actualTotalMoney = team.cashMoney + currentStockValuation

        // 이전 차수 기준 총 자산 = 현금 + 이전 차수 주식 평가액
        val previousTotalMoney = team.cashMoney + previousStockValuation

        // 총 자산 증감률 (이전 vs 현재 차수)
        val currentTotalValProfit = actualTotalMoney - previousTotalMoney

        val profitNum = if (previousTotalMoney > 0) {
            (currentTotalValProfit.toDouble() / previousTotalMoney.toDouble()) * 100
        } else {
            0.0
        }

        val formattedProfitNum = when {
            profitNum > 0 -> "+%.2f%%".format(profitNum)
            else -> "%.2f%%".format(profitNum)
        }

        return TeamDetailResponse(
            id = teamId,
            teamName = team.teamName,
            baseMoney = lesson.baseMoney,
            totalMoney = actualTotalMoney,
            cashMoney = team.cashMoney,
            valuationMoney = currentStockValuation,
            curInvRound = currentRound,
            maxInvRound = lesson.maxInvRound,
            valProfit = currentTotalValProfit,
            profitNum = formattedProfitNum
        )
    }
}
