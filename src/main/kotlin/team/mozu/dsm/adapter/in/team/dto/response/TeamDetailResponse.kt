package team.mozu.dsm.adapter.`in`.team.dto.response

import team.mozu.dsm.domain.lesson.model.Lesson
import team.mozu.dsm.domain.team.model.Team
import java.util.UUID

data class TeamDetailResponse(
    val id: UUID,
    val teamName: String?,
    val baseMoney: Long,
    val totalMoney: Long,
    val cashMoney: Long,
    val valuationMoney: Long,
    val curInvRound: Int,
    val maxInvRound: Int,
    val valProfit: Long,
    val profitNum: String
) {
    companion object {
        fun of(
            team: Team,
            lesson: Lesson,
            currentStockValuation: Long,
            previousStockValuation: Long
        ): TeamDetailResponse {
            val actualTotalMoney = team.cashMoney + currentStockValuation
            val previousTotalMoney = team.cashMoney + previousStockValuation
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
                id = team.id!!,
                teamName = team.teamName,
                baseMoney = lesson.baseMoney,
                totalMoney = actualTotalMoney,
                cashMoney = team.cashMoney,
                valuationMoney = currentStockValuation,
                curInvRound = lesson.curInvRound,
                maxInvRound = lesson.maxInvRound,
                valProfit = currentTotalValProfit,
                profitNum = formattedProfitNum
            )
        }
    }
}
