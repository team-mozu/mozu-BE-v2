package team.mozu.dsm.domain.lesson.model

import team.mozu.dsm.domain.annotation.Aggregate
import team.mozu.dsm.domain.lesson.model.id.LessonItemId

@Aggregate
data class LessonItem(
    val lessonItemId: LessonItemId,
    val currentMoney: Int,
    val round1Money: Int,
    val round2Money: Int,
    val round3Money: Int,
    val round4Money: Int?,
    val round5Money: Int?
)
