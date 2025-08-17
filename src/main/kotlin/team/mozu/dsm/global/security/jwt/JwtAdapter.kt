package team.mozu.dsm.global.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse
import team.mozu.dsm.adapter.out.auth.entity.RefreshTokenRedisEntity
import team.mozu.dsm.adapter.out.auth.persistence.repository.RefreshTokenRepository
import team.mozu.dsm.application.port.out.auth.JwtPort
import team.mozu.dsm.global.exception.ExpiredTokenException
import team.mozu.dsm.global.exception.InvalidTokenException
import team.mozu.dsm.global.security.auth.CustomUserDetailsService
import java.nio.charset.StandardCharsets
import java.security.Key
import java.time.LocalDateTime
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtAdapter(
    private val jwtProperties: JwtProperties,
    private val customUserDetailsService: CustomUserDetailsService,
    private val refreshTokenRepository: RefreshTokenRepository
) : JwtPort {
    companion object {
        private const val TYPE_CLAIM = "type"
        private const val ACCESS_TYPE = "access"
        private const val REFRESH_TYPE = "refresh"
        private const val MILLISECONDS = 1000
    }

    private val secretKey: Key = try {
        Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.secretKey))
    } catch (e: IllegalArgumentException) {
        Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8))
    }

    private fun generateToken(organCode: String, type: String, expirationSeconds: Long): String {
        val now = Date()
        return Jwts.builder()
            .setSubject(organCode)
            .claim(TYPE_CLAIM, type)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + expirationSeconds * MILLISECONDS))
            .signWith(secretKey)
            .compact()
    }

    // 내부에서만 사용
    private fun generateAccessToken(organCode: String): String =
        generateToken(organCode, ACCESS_TYPE, jwtProperties.accessExpiration)

    // 내부에서만 사용
    private fun generateRefreshToken(organCode: String): String {
        val refreshToken = generateToken(organCode, REFRESH_TYPE, jwtProperties.refreshExpiration)

        refreshTokenRepository.save(
            RefreshTokenRedisEntity(
                organCode = organCode,
                token = refreshToken,
                timeToLive = jwtProperties.refreshExpiration
            )
        )
        return refreshToken
    }

    // 외부 호출용 메서드
    override fun createToken(organCode: String): TokenResponse {
        val now = LocalDateTime.now()

        return TokenResponse(
            accessToken = generateAccessToken(organCode),
            refreshToken = generateRefreshToken(organCode),
            accessExpiredAt = now.plusSeconds(jwtProperties.accessExpiration),
            refreshExpiredAt = now.plusSeconds(jwtProperties.refreshExpiration)
        )
    }

    override fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val userDetails: UserDetails =
            customUserDetailsService.loadUserByUsername(claims.subject)
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getClaims(token: String): Claims {
        return try {
            val parser = Jwts.parser()
                .verifyWith(secretKey as SecretKey)
                .build()

            parser.parseSignedClaims(token).payload
        } catch (e: ExpiredJwtException) {
            throw ExpiredTokenException
        } catch (e: Exception) {
            throw InvalidTokenException
        }
    }

    override fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(jwtProperties.header) ?: return null
        val prefix = jwtProperties.prefix

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(prefix)) {
            val token = bearerToken.substring(prefix.length).trim()
            if (token.isNotEmpty()) return token
        }
        return null
    }
}
