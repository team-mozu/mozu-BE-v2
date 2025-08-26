package team.mozu.dsm.application.port.out.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse
import team.mozu.dsm.application.port.`in`.team.dto.response.TeamToken

interface JwtPort {

    fun createToken(organCode: String): TokenResponse

    fun getAuthentication(token: String): Authentication

    fun resolveToken(request: HttpServletRequest): String?

    fun createStudentAccessToken(lessonNum: String): TeamToken
}
