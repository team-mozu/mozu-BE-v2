package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundArticleResponse

interface GetLessonRoundArticlesUseCase {

    fun get(lessonNum: String): List<LessonRoundArticleResponse>
}
