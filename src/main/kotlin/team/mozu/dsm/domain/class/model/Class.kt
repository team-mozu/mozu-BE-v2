package team.mozu.dsm.domain.`class`.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Class(
    val id: UUID?,
    val organId: UUID,
    val className: String,
    val maxInvRound: Int,
    val curInvRound: Int,
    val baseMoney: Int,
    val classNum: String,
    val isStarred: Boolean,
    val isDeleted: Boolean,
    val isInProgress: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
