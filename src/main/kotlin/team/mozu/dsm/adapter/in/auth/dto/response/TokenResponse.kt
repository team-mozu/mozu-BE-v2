package team.mozu.dsm.adapter.`in`.auth.dto.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessExpiredAt: Long,
    val refreshExpiredAt: Long
)
