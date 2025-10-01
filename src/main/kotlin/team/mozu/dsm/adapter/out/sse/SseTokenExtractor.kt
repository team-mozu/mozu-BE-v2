package team.mozu.dsm.adapter.out.sse

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * SSE 연결 요청에서 JWT 토큰을 추출하는 유틸리티 클래스
 */
@Component
class SseTokenExtractor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private const val TOKEN_QUERY_PARAM = "token"
    }

    /**
     * HTTP 요청에서 JWT 토큰을 추출
     * 1. Authorization 헤더에서 Bearer 토큰 확인
     * 2. 쿼리 파라미터에서 token 확인 (SSE 연결 시 헤더 설정이 어려운 경우)
     * * @param request HTTP 요청
     * @return JWT 토큰 또는 null
     */
    fun extractToken(request: HttpServletRequest): String? {
        // 1. Authorization 헤더에서 토큰 추출
        val authHeader = request.getHeader(AUTHORIZATION_HEADER)
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            val token = authHeader.substring(BEARER_PREFIX.length).trim()
            if (token.isNotEmpty()) {
                return token
            }
        }

        // 2. 쿼리 파라미터에서 토큰 추출 (SSE 연결 시 대안)
        val tokenParam = request.getParameter(TOKEN_QUERY_PARAM)
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam.trim()
        }

        return null
    }

    /**
     * 토큰이 Bearer 형식인지 확인
     */
    fun isBearerToken(authHeader: String?): Boolean {
        return StringUtils.hasText(authHeader) && authHeader!!.startsWith(BEARER_PREFIX)
    }
}
