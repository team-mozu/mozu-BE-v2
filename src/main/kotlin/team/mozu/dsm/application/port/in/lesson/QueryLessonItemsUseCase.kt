package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemResponse
import java.util.UUID

interface QueryLessonItemsUseCase {

    fun execute(lessonId: UUID): List<LessonItemResponse>
}
