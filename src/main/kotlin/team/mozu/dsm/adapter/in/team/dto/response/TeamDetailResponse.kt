package team.mozu.dsm.adapter.`in`.team.dto.response

import java.util.UUID

data class TeamDetailResponse(
    val id: UUID,
    val teamName: String?,
    val baseMoney: Long,
    val totalMoney: Long,
    val cashMoney: Long,
    val valuationMoney: Long,
    val curInvRound: Int,
    val valProfit: Int,
    val profitNum: Double
)
