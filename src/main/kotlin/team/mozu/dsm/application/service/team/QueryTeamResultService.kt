package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamResultResponse
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.QueryTeamResultUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryOrderItemPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class QueryTeamResultService(
    private val queryLessonPort: QueryLessonPort,
    private val queryTeamPort: QueryTeamPort,
    private val queryStockPort: QueryStockPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryOrderItemPort: QueryOrderItemPort
) : QueryTeamResultUseCase {

    @Transactional(readOnly = true)
    override fun execute(lessonNum: String, teamId: UUID): TeamResultResponse {
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

        // 수익 계산을 위한 차수: 현재 진행 차수(N) + 1
        val profitCalculationRound = lesson.curInvRound + 1

        // 주식 평가액 계산 (N+1 차수 기준)
        val currentStockValuation = stocks.sumOf { stock ->
            val lessonItem = lessonItemMap[stock.itemId] ?: return@sumOf 0L
            val currentPrice = lessonItem.getPriceByRound(profitCalculationRound) ?: lessonItem.endPrice
            currentPrice * stock.quantity
        }

        // 실제 총 자산 = 현금 + 주식 평가액
        val actualTotalMoney = team.cashMoney + currentStockValuation

        // 평가손익 = 총 자산 - 초기 자산
        val valProfit = actualTotalMoney - lesson.baseMoney

        // 수익률 = ((최종 자산 - 초기 자산) / 초기 자산) * 100
        val profitNum = if (lesson.baseMoney > 0) {
            ((actualTotalMoney.toDouble() - lesson.baseMoney.toDouble()) / lesson.baseMoney.toDouble()) * 100
        } else {
            0.0
        }

        val formattedProfitNum = when {
            profitNum > 0 -> "+%.2f%%".format(profitNum)
            else -> "%.2f%%".format(profitNum)
        }

        return TeamResultResponse(
            id = team.id!!,
            teamName = team.teamName,
            baseMoney = lesson.baseMoney,
            totalMoney = actualTotalMoney,
            investingMoney = totalBuyMoney, // 투자중인 금액 (매입한 주식 총액)
            availableMoney = team.cashMoney, // 주문 가능 금액 (보유 현금)
            invRound = lesson.curInvRound,
            valProfit = valProfit,
            profitNum = formattedProfitNum,
            orderCount = queryOrderItemPort.countOrderItemsByTeamId(teamId)
        )
    }
}
