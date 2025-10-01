package team.mozu.dsm.application.port.`in`.team

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.team.dto.TeamInvestmentCompletedEventDTO
import team.mozu.dsm.global.security.auth.StudentPrincipal
import java.util.UUID

/**
 * 팀 SSE 관련 기능을 제공하는 서비스 인터페이스
 */
interface TeamSseService {

    /**
     * 팀 SSE 연결을 설정합니다.
     * @param teamId 팀 ID
     * @param principal 인증된 학생 정보
     * @param lastEventId 마지막으로 받은 이벤트 ID (재연결 시)
     * @param isReconnection 재연결 여부
     * @return SSE Emitter
     */
    fun connectTeamSSE(
        teamId: UUID,
        principal: StudentPrincipal,
        lastEventId: String? = null,
        isReconnection: Boolean = false
    ): SseEmitter

    /**
     * JWT 토큰을 사용하여 팀 SSE 연결을 설정합니다.
     * @param teamId 팀 ID
     * @param request HTTP 요청 (토큰 추출용)
     * @param lastEventId 마지막으로 받은 이벤트 ID (재연결 시)
     * @param isReconnection 재연결 여부
     * @return SSE Emitter
     */
    fun connectTeamSSEWithToken(
        teamId: UUID,
        request: HttpServletRequest,
        lastEventId: String? = null,
        isReconnection: Boolean = false
    ): SseEmitter

    /**
     * 투자 완료를 알립니다.
     * @param teamId 팀 ID
     * @param investmentData 투자 완료 데이터
     */
    fun notifyInvestmentComplete(teamId: UUID, investmentData: TeamInvestmentCompletedEventDTO)

    /**
     * 팀 연결 해제를 처리합니다.
     * @param connectionId 연결 ID
     */
    fun handleTeamDisconnection(connectionId: String)
}
