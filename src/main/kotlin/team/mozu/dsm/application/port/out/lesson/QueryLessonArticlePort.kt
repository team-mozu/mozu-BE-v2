package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundArticleResponse
import team.mozu.dsm.domain.lesson.model.LessonArticle
import java.util.*

interface QueryLessonArticlePort {

    fun findAllByLessonId(lessonId: UUID): List<LessonArticle>

    fun findAllRoundArticlesByLessonId(lessonId: UUID, nowInvRound: Int): List<LessonRoundArticleResponse>
}
