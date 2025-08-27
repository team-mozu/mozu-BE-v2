package team.mozu.dsm.domain.lesson.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Lesson(
    val id: UUID? = null,
    val organId: UUID,
    val lessonName: String,
    val maxInvRound: Int,
    val curInvRound: Int,
    val baseMoney: Long,
    val lessonNum: String? = null,
    val isStarred: Boolean,
    val isDeleted: Boolean,
    val isInProgress: Boolean,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
