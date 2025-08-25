package team.mozu.dsm.adapter.`in`.team.dto.response

import java.time.LocalDateTime

data class TeamTokenResponse(
    val accessToken: String,
    val accessExpiredAt: LocalDateTime
)
