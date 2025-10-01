package team.mozu.dsm.application.port.out.sse

import java.util.UUID

/**
 * SSE 연결 및 이벤트 전송에 대한 Rate Limiting 서비스 인터페이스
 */
interface SseRateLimitService {

    /**
     * 클라이언트의 연결 시도를 검증합니다.
     * @param clientId 클라이언트 식별자 (IP 주소 또는 사용자 ID)
     * @param resourceType 리소스 타입 (TEAM_SSE, ADMIN_SSE)
     * @return 연결 허용 여부
     */
    fun allowConnection(clientId: String, resourceType: String): Boolean

    /**
     * 클라이언트의 이벤트 전송을 검증합니다.
     * @param clientId 클라이언트 식별자
     * @param eventType 이벤트 타입
     * @return 이벤트 전송 허용 여부
     */
    fun allowEventSend(clientId: String, eventType: String): Boolean

    /**
     * 특정 팀의 동시 연결 수를 확인합니다.
     * @param teamId 팀 ID
     * @return 현재 연결 수
     */
    fun getTeamConnectionCount(teamId: UUID): Int

    /**
     * 특정 수업의 관리자 연결 수를 확인합니다.
     * @param lessonId 수업 ID
     * @return 현재 연결 수
     */
    fun getAdminConnectionCount(lessonId: UUID): Int

    /**
     * 전체 시스템의 연결 수를 확인합니다.
     * @return 현재 전체 연결 수
     */
    fun getTotalConnectionCount(): Int

    /**
     * 클라이언트의 연결 시도 기록을 업데이트합니다.
     * @param clientId 클라이언트 식별자
     * @param resourceType 리소스 타입
     */
    fun recordConnectionAttempt(clientId: String, resourceType: String)

    /**
     * 클라이언트의 이벤트 전송 기록을 업데이트합니다.
     * @param clientId 클라이언트 식별자
     * @param eventType 이벤트 타입
     */
    fun recordEventSend(clientId: String, eventType: String)

    /**
     * 클라이언트의 Rate Limit 상태를 초기화합니다.
     * @param clientId 클라이언트 식별자
     */
    fun resetClientLimits(clientId: String)

    /**
     * 시스템 부하에 따라 동적으로 제한을 조정합니다.
     * @param systemLoad 시스템 부하 (0.0 ~ 1.0)
     */
    fun adjustLimitsBasedOnLoad(systemLoad: Double)

    /**
     * Rate Limit 위반 시도를 로깅합니다.
     * @param clientId 클라이언트 식별자
     * @param resourceType 리소스 타입
     * @param reason 위반 사유
     */
    fun logRateLimitViolation(clientId: String, resourceType: String, reason: String)
}
