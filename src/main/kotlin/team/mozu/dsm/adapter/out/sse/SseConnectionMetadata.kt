package team.mozu.dsm.adapter.out.sse

import team.mozu.dsm.domain.sse.model.ConnectionType
import java.time.LocalDateTime
import java.util.UUID

/**
 * SSE 연결 메타데이터
 */
data class SseConnectionMetadata(
    val id: String,
    val teamId: UUID? = null,
    val lessonId: UUID? = null,
    val connectionType: ConnectionType,
    val connectedAt: LocalDateTime,
    val lastActivity: LocalDateTime,
    val isActive: Boolean
)
