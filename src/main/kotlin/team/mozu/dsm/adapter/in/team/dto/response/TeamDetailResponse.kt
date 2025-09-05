package team.mozu.dsm.adapter.`in`.team.dto.response

import java.util.UUID

data class TeamDetailResponse(
    val id: UUID,
    val teamName: String?,
    val baseMoney: Int,
    val totalMoney: Int,
    val cashMoney: Int,
    val valuationMoney: Int,
    val curInvRound: Int,
    val valProfit: Int,
    val profitNum: Double
)
