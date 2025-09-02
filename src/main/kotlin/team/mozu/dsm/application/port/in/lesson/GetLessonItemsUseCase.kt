package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemResponse
import java.util.UUID

interface GetLessonItemsUseCase {

    fun get(lessonId: UUID): List<LessonItemResponse>
}
