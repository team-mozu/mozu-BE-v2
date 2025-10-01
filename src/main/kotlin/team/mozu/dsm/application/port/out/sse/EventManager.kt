package team.mozu.dsm.application.port.out.sse

import team.mozu.dsm.domain.sse.model.SseEvent
import team.mozu.dsm.domain.sse.model.SseEventType
import java.time.LocalDateTime
import java.util.UUID

/**
 * SSE 이벤트의 생성, 전송, 관리를 담당하는 인터페이스
 */
interface EventManager {

    /**
     * SSE 이벤트를 생성합니다.
     * @param type 이벤트 타입
     * @param data 이벤트 데이터
     * @param lessonId 수업 ID (선택사항)
     * @param teamId 팀 ID (선택사항)
     * @return 생성된 SSE 이벤트
     */
    fun createEvent(
        type: SseEventType,
        data: Any,
        lessonId: UUID? = null,
        teamId: UUID? = null
    ): SseEvent

    /**
     * 팀에게 이벤트를 발행합니다.
     * @param teamId 팀 ID
     * @param event SSE 이벤트
     */
    fun publishTeamEvent(teamId: UUID, event: SseEvent)

    /**
     * 관리자에게 이벤트를 발행합니다.
     * @param lessonId 수업 ID
     * @param event SSE 이벤트
     */
    fun publishAdminEvent(lessonId: UUID, event: SseEvent)

    /**
     * 모든 팀에게 브로드캐스트 이벤트를 발행합니다.
     * @param lessonId 수업 ID
     * @param event SSE 이벤트
     */
    fun publishBroadcastEvent(lessonId: UUID, event: SseEvent)

    // 이벤트 히스토리 관리 (재연결 시 복구용)
    /**
     * 이벤트 히스토리를 저장합니다.
     * @param lessonId 수업 ID
     * @param event SSE 이벤트
     */
    fun saveEventHistory(lessonId: UUID, event: SseEvent)

    /**
     * 특정 시점 이후의 이벤트 히스토리를 조회합니다.
     * @param lessonId 수업 ID
     * @param since 조회 시작 시점
     * @return 이벤트 히스토리 목록
     */
    fun getEventHistory(lessonId: UUID, since: LocalDateTime): List<SseEvent>

    /**
     * 이벤트 히스토리를 정리합니다.
     * @param lessonId 수업 ID
     * @param before 정리 기준 시점
     */
    fun cleanupEventHistory(lessonId: UUID, before: LocalDateTime)
}
