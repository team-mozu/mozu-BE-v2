package team.mozu.dsm.domain.team.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class RoundSnapshot(
    val id: UUID?,
    val lessonId: UUID,
    val teamId: UUID,
    val invRound: Int,
    val totalMoney: Long,
    val cashMoney: Long,
    val valuationMoney: Long,
    val profitNum: Double,
    val endedAt: LocalDateTime,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
