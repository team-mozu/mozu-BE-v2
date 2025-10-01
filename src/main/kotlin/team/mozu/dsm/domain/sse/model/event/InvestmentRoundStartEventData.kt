package team.mozu.dsm.domain.sse.model.event

import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

/**
 * 투자 라운드 시작 이벤트 데이터
 */
data class InvestmentRoundStartEventData(
    val lessonId: UUID,
    val roundNumber: Int,
    val startTime: LocalDateTime,
    val timeLimit: Duration? = null
)
