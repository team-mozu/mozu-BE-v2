package team.mozu.dsm.domain.lesson.model.id

import java.util.UUID

data class LessonArticleId(
    val lessonId: UUID,
    val articleId: UUID
)
