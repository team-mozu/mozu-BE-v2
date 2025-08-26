package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.domain.lesson.model.LessonArticle
import java.util.UUID

interface CommandLessonArticlePort {

    fun saveAll(id: UUID, lessonArticles: List<LessonArticle>): List<LessonArticle>
}
