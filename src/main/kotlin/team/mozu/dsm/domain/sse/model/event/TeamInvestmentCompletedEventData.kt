package team.mozu.dsm.domain.sse.model.event

import java.time.LocalDateTime
import java.util.UUID

/**
 * 팀 투자 완료 이벤트 데이터
 */
data class TeamInvestmentCompletedEventData(
    val teamId: UUID,
    val teamName: String?,
    val curInvRound: Int,
    val totalMoney: Long,
    val valuationMoney: Long,
    val profitNum: String,
    val completedAt: LocalDateTime
)
