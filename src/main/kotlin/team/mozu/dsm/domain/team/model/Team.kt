package team.mozu.dsm.domain.team.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Team(
    val id: UUID?,
    val classId: UUID,
    val teamName: String?,
    val schoolName: String,
    val totalMoney: Int,
    val cashMoney: Int,
    val valuationMoney: Int,
    val classNumber: String,
    val investmentCompleteYn: Boolean,
    val participationDate: LocalDateTime,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
