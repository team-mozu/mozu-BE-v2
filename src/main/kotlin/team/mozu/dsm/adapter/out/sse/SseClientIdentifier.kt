package team.mozu.dsm.adapter.out.sse

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import team.mozu.dsm.global.security.auth.StudentPrincipal
import java.util.UUID

/**
 * SSE 클라이언트 식별을 위한 유틸리티 클래스
 */
@Component
class SseClientIdentifier {

    /**
     * HTTP 요청에서 클라이언트 ID를 추출합니다.
     * 우선순위: 사용자 ID > 세션 ID > IP 주소
     */
    fun extractClientId(request: HttpServletRequest, principal: StudentPrincipal? = null): String {
        // 1. 인증된 사용자가 있는 경우 사용자 정보 사용
        principal?.let {
            return "user:${it.lessonNum}:${it.teamId}"
        }

        // 2. 세션 ID 사용
        request.session?.let { session ->
            if (!session.isNew) {
                return "session:${session.id}"
            }
        }

        // 3. IP 주소 사용 (프록시 고려)
        val clientIp = getClientIpAddress(request)
        return "ip:$clientIp"
    }

    /**
     * 팀 기반 클라이언트 ID 생성
     */
    fun generateTeamClientId(teamId: UUID, lessonNum: String): String {
        return "team:$lessonNum:$teamId"
    }

    /**
     * 관리자 기반 클라이언트 ID 생성
     */
    fun generateAdminClientId(organCode: String, lessonId: UUID): String {
        return "admin:$organCode:$lessonId"
    }

    /**
     * 클라이언트 IP 주소 추출 (프록시 환경 고려)
     */
    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (!xForwardedFor.isNullOrBlank()) {
            // 첫 번째 IP가 실제 클라이언트 IP
            return xForwardedFor.split(",")[0].trim()
        }

        val xRealIp = request.getHeader("X-Real-IP")
        if (!xRealIp.isNullOrBlank()) {
            return xRealIp.trim()
        }

        val xForwardedProto = request.getHeader("X-Forwarded-Proto")
        if (!xForwardedProto.isNullOrBlank()) {
            return request.remoteAddr
        }

        return request.remoteAddr ?: "unknown"
    }

    /**
     * 클라이언트 ID에서 타입 추출
     */
    fun getClientType(clientId: String): String {
        return when {
            clientId.startsWith("user:") -> "USER"
            clientId.startsWith("team:") -> "TEAM"
            clientId.startsWith("admin:") -> "ADMIN"
            clientId.startsWith("session:") -> "SESSION"
            clientId.startsWith("ip:") -> "IP"
            else -> "UNKNOWN"
        }
    }

    /**
     * 클라이언트 ID가 특정 사용자/팀에 속하는지 확인
     */
    fun belongsToTeam(clientId: String, teamId: UUID): Boolean {
        return clientId.contains(teamId.toString())
    }

    /**
     * 클라이언트 ID가 특정 수업에 속하는지 확인
     */
    fun belongsToLesson(clientId: String, lessonNum: String): Boolean {
        return clientId.contains(lessonNum)
    }
}
