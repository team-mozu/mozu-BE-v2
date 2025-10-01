package team.mozu.dsm.domain.sse.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * SSE 이벤트를 나타내는 데이터 클래스
 */
data class SseEvent(
    val id: String = UUID.randomUUID().toString(),
    val type: SseEventType,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val data: Any,
    val lessonId: UUID? = null,
    val teamId: UUID? = null
)
