package team.mozu.dsm.domain.sse.model.event

import java.time.LocalDateTime
import java.util.UUID

/**
 * 전체 완료 이벤트 데이터
 */
data class AllFinishedEventData(
    val lessonId: UUID,
    val completedTeams: Int,
    val totalTeams: Int,
    val completedAt: LocalDateTime
)
