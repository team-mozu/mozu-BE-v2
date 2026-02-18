package team.mozu.dsm.adapter.`in`.team.dto.response

import team.mozu.dsm.domain.lesson.model.Lesson
import team.mozu.dsm.domain.team.model.Team
import java.util.UUID

data class TeamResultResponse(
    val id: UUID,
    val teamName: String?,
    val baseMoney: Long,
    val totalMoney: Long,
    val investingMoney: Long, // 투자중인 금액 (매입한 주식 총액)
    val availableMoney: Long, // 주문 가능 금액 (보유 현금)
    val invRound: Int,
    val valProfit: Long,
    val profitNum: String,
    val orderCount: Int
) {
    companion object {
        fun of(
            team: Team,
            lesson: Lesson,
            currentStockValuation: Long,
            totalBuyMoney: Long,
            orderCount: Int
        ): TeamResultResponse {
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
                investingMoney = totalBuyMoney,
                availableMoney = team.cashMoney,
                invRound = lesson.curInvRound,
                valProfit = valProfit,
                profitNum = formattedProfitNum,
                orderCount = orderCount
            )
        }
    }
}
