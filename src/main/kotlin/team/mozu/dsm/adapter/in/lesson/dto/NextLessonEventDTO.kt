package team.mozu.dsm.adapter.`in`.lesson.dto

import java.util.UUID

data class NextLessonEventDTO(
    val lessonId: UUID,
    val curInvRound: Int,
    val teamId: UUID,
    val teamName: String?,
    val schoolName: String
)
