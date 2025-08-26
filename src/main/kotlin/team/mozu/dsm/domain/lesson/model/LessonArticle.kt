package team.mozu.dsm.domain.lesson.model

import team.mozu.dsm.domain.lesson.model.id.LessonArticleId

data class LessonArticle(
    val lessonArticleId: LessonArticleId,
    val investmentRound: Int
)
