package team.mozu.dsm.adapter.`in`.team.dto

import java.util.UUID

data class TeamInvestmentCompletedEventDTO(
    val teamId: UUID,
    val teamName: String?,
    val curInvRound: Int,
    val totalMoney: Long,
    val valuationMoney: Long,
    val profitNum: Double,
)
