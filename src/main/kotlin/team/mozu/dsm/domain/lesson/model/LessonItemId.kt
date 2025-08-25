package team.mozu.dsm.domain.lesson.model

import java.util.UUID

data class LessonItemId(
    val lessonId: UUID,
    val itemId: UUID
)
