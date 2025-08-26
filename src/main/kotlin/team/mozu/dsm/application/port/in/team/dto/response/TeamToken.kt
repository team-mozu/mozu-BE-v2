package team.mozu.dsm.application.port.`in`.team.dto.response

import java.time.LocalDateTime

data class TeamToken(
    val accessToken: String,
    val accessExpiredAt: LocalDateTime
)
