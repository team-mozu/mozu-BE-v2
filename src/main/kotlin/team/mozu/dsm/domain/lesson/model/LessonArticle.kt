package team.mozu.dsm.domain.lesson.model

import java.util.UUID

data class LessonArticle(
    val lessonId: UUID,
    val articleId: UUID,
    val investmentRound: Int
)
