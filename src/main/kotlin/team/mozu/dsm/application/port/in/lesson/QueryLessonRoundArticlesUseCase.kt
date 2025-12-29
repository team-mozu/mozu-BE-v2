package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundArticleResponse

interface QueryLessonRoundArticlesUseCase {

    fun execute(lessonNum: String): List<LessonRoundArticleResponse>
}
