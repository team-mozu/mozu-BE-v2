package team.mozu.dsm.domain.lesson.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.util.UUID

@Aggregate
data class LessonItem(
    val lessonId: UUID,
    val itemId: UUID,
    val currentMoney: Int,
    val round1Money: Int,
    val round2Money: Int,
    val round3Money: Int,
    val round4Money: Int?,
    val round5Money: Int?,
    val round6Money: Int?,
    val round7Money: Int?,
    val round8Money: Int?,
)
