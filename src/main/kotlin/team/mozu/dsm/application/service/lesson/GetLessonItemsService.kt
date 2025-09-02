package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemResponse
import team.mozu.dsm.application.port.`in`.lesson.GetLessonItemsUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class GetLessonItemsService(
    private val lessonItemPort: QueryLessonItemPort,
    private val lessonFacade: LessonFacade
): GetLessonItemsUseCase {

    override fun get(lessonId: UUID): List<LessonItemResponse> {
        val lessonItems = lessonItemPort.findAllByLessonId(lessonId)

        return lessonFacade.toLessonItemResponses(lessonItems)
    }
}
