package team.mozu.dsm.application.port.out.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse

interface JwtPort {

    fun createToken(organCode: String): TokenResponse

    fun getAuthentication(token: String): Authentication

    fun resolveToken(request: HttpServletRequest): String?
}
