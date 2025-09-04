package team.mozu.dsm.adapter.`in`.team.dto.response

import java.util.UUID

data class TeamRankResponse(
    val id: UUID,
    val teamName: String?,
    val schoolName: String,
    val totalMoney: Int,
    val isMyTeam: Boolean
)
