package team.mozu.dsm.application.port.out.sse

import team.mozu.dsm.global.security.auth.StudentPrincipal
import java.util.UUID

/**
 * SSE 연결을 위한 인증 서비스 인터페이스
 */
interface SseAuthenticationService {

    /**
     * JWT 토큰을 검증하고 StudentPrincipal을 반환
     * @param token JWT 토큰
     * @return StudentPrincipal 또는 null (인증 실패 시)
     */
    fun validateToken(token: String): StudentPrincipal?

    /**
     * 팀 SSE 연결 권한을 검증
     * @param principal 인증된 사용자 정보
     * @param teamId 접근하려는 팀 ID
     * @return 권한 여부
     */
    fun validateTeamAccess(principal: StudentPrincipal, teamId: UUID): Boolean

    /**
     * 관리자 SSE 연결 권한을 검증
     * @param principal 인증된 사용자 정보
     * @param lessonId 접근하려는 수업 ID
     * @return 권한 여부
     */
    fun validateAdminAccess(principal: StudentPrincipal, lessonId: UUID): Boolean

    /**
     * 토큰 만료 여부 확인
     * @param token JWT 토큰
     * @return 만료 여부
     */
    fun isTokenExpired(token: String): Boolean

    /**
     * 권한 없는 접근 시도를 로깅
     * @param principal 사용자 정보
     * @param resourceType 접근하려는 리소스 타입
     * @param resourceId 리소스 ID
     * @param reason 차단 사유
     */
    fun logUnauthorizedAccess(principal: StudentPrincipal?, resourceType: String, resourceId: String, reason: String)
}
