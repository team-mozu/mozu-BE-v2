package team.mozu.dsm.adapter.`in`.team.dto.response

import java.util.UUID

data class TeamResultResponse(
    val id: UUID,
    val teamName: String?,
    val baseMoney: Long,
    val totalMoney: Long,
    val invRound: Int,
    val valProfit: Long,
    val profitNum: Double,
    val orderCount: Int
)
