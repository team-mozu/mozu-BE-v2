package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemResponse
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonItemsUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class QueryLessonItemsService(
    private val lessonItemPort: QueryLessonItemPort,
    private val lessonFacade: LessonFacade
) : QueryLessonItemsUseCase {

    override fun execute(lessonId: UUID): List<LessonItemResponse> {
        lessonFacade.findByLessonId(lessonId)
        val lessonItems = lessonItemPort.findAllByLessonId(lessonId)

        return lessonFacade.toLessonItemResponses(lessonItems)
    }
}
