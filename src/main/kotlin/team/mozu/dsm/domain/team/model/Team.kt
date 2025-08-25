package team.mozu.dsm.domain.team.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Team(
    val id: UUID?,
    val lessonId: UUID,
    val teamName: String?,
    val schoolName: String,
    val totalMoney: Long,
    val cashMoney: Long,
    val valuationMoney: Long,
    val lessonNum: String,
    val isInvestmentInProgress: Boolean,
    val participationDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
