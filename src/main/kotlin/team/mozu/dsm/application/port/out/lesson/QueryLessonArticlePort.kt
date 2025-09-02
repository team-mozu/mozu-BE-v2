package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.domain.lesson.model.LessonArticle
import java.util.*

interface QueryLessonArticlePort {

    fun findAllByLessonId(lessonId: UUID): List<LessonArticle>
}
