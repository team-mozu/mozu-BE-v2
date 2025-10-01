package team.mozu.dsm.domain.sse.model

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.time.LocalDateTime
import java.util.UUID

/**
 * SSE 연결 정보를 나타내는 데이터 클래스
 */
data class SseConnection(
    val id: String,
    val emitter: SseEmitter,
    val teamId: UUID? = null,
    val lessonId: UUID? = null,
    val connectionType: ConnectionType,
    val connectedAt: LocalDateTime,
    val lastActivity: LocalDateTime,
    val isActive: Boolean = true
)
