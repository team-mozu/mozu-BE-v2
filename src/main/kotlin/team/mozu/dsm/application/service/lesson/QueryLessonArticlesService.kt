package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonArticleResponse
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonArticlesUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonArticlePort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class QueryLessonArticlesService(
    private val lessonArticlePort: QueryLessonArticlePort,
    private val lessonFacade: LessonFacade
) : QueryLessonArticlesUseCase {

    @Transactional(readOnly = true)
    override fun execute(lessonId: UUID): List<LessonArticleResponse> {
        lessonFacade.findByLessonId(lessonId)
        val lessonArticles = lessonArticlePort.findAllByLessonId(lessonId)

        return lessonFacade.toLessonArticleResponses(lessonArticles)
    }
}
