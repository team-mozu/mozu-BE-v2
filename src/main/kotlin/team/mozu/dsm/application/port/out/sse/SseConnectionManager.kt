package team.mozu.dsm.application.port.out.sse

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.domain.sse.model.ConnectionStats
import team.mozu.dsm.domain.sse.model.ConnectionStatus
import team.mozu.dsm.domain.sse.model.ReconnectionGuide
import team.mozu.dsm.domain.sse.model.SseConnection
import team.mozu.dsm.domain.sse.model.SseEvent
import java.time.LocalDateTime
import java.util.UUID

/**
 * SSE 연결을 중앙에서 관리하는 인터페이스
 */
interface SseConnectionManager {

    // 팀 SSE 연결 관리
    /**
     * 팀 SSE 연결을 추가합니다.
     * @param teamId 팀 ID
     * @param emitter SSE Emitter
     * @return 연결 ID
     */
    fun addTeamConnection(teamId: UUID, emitter: SseEmitter): String

    /**
     * SSE 연결을 제거합니다.
     * @param connectionId 연결 ID
     */
    fun removeTeamConnection(connectionId: String)

    /**
     * 특정 팀의 모든 연결을 조회합니다.
     * @param teamId 팀 ID
     * @return 팀의 SSE 연결 목록
     */
    fun getTeamConnections(teamId: UUID): List<SseConnection>

    /**
     * 모든 팀 연결을 조회합니다.
     * @return 모든 팀 SSE 연결 목록
     */
    fun getAllTeamConnections(): List<SseConnection>

    // 관리자 SSE 연결 관리
    /**
     * 관리자 SSE 연결을 추가합니다.
     * @param lessonId 수업 ID
     * @param emitter SSE Emitter
     * @return 연결 ID
     */
    fun addAdminConnection(lessonId: UUID, emitter: SseEmitter): String

    /**
     * 관리자 SSE 연결을 제거합니다.
     * @param connectionId 연결 ID
     */
    fun removeAdminConnection(connectionId: String)

    /**
     * 특정 수업의 관리자 연결을 조회합니다.
     * @param lessonId 수업 ID
     * @return 관리자 SSE 연결 목록
     */
    fun getAdminConnections(lessonId: UUID): List<SseConnection>

    // 이벤트 전송
    /**
     * 특정 팀에게 이벤트를 전송합니다.
     * @param teamId 팀 ID
     * @param event SSE 이벤트
     */
    fun sendToTeam(teamId: UUID, event: SseEvent)

    /**
     * 특정 수업의 모든 팀에게 이벤트를 전송합니다.
     * @param lessonId 수업 ID
     * @param event SSE 이벤트
     */
    fun sendToAllTeams(lessonId: UUID, event: SseEvent)

    /**
     * 특정 수업의 관리자에게 이벤트를 전송합니다.
     * @param lessonId 수업 ID
     * @param event SSE 이벤트
     */
    fun sendToAdmin(lessonId: UUID, event: SseEvent)

    // 연결 상태 관리
    /**
     * 죽은 연결들을 정리합니다.
     */
    fun cleanupDeadConnections()

    /**
     * 연결 통계를 조회합니다.
     * @return 연결 통계 정보
     */
    fun getConnectionStats(): ConnectionStats

    /**
     * 특정 연결의 활성 상태를 업데이트합니다.
     * @param connectionId 연결 ID
     */
    fun updateLastActivity(connectionId: String)

    /**
     * 특정 연결이 활성 상태인지 확인합니다.
     * @param connectionId 연결 ID
     * @return 활성 상태 여부
     */
    fun isConnectionActive(connectionId: String): Boolean

    // 헬스체크 및 오류 처리
    /**
     * 특정 연결에 대해 헬스체크를 수행합니다.
     * @param connectionId 연결 ID
     * @return 헬스체크 성공 여부
     */
    fun performHealthCheck(connectionId: String): Boolean

    /**
     * 모든 연결에 대해 헬스체크를 수행합니다.
     * @return 헬스체크 결과 (연결 ID -> 성공 여부)
     */
    fun performHealthCheckAll(): Map<String, Boolean>

    /**
     * 연결 오류를 처리합니다.
     * @param connectionId 연결 ID
     * @param error 발생한 오류
     */
    fun handleConnectionError(connectionId: String, error: Throwable)

    /**
     * 비정상 연결을 감지하고 정리합니다.
     * @return 정리된 연결 수
     */
    fun detectAndCleanupAbnormalConnections(): Int

    // 재연결 지원
    /**
     * 재연결 시 놓친 이벤트를 복구합니다.
     * @param connectionId 연결 ID
     * @param lastEventTime 마지막으로 받은 이벤트 시간
     * @return 복구된 이벤트 수
     */
    fun recoverMissedEvents(connectionId: String, lastEventTime: LocalDateTime): Int

    /**
     * 연결 상태 피드백을 전송합니다.
     * @param connectionId 연결 ID
     * @param status 연결 상태
     */
    fun sendConnectionStatusFeedback(connectionId: String, status: ConnectionStatus)

    /**
     * 재연결 가이드를 제공합니다.
     * @param connectionId 연결 ID
     * @return 재연결 가이드 정보
     */
    fun getReconnectionGuide(connectionId: String): ReconnectionGuide
}
