package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import java.util.UUID

interface UpdateLessonUseCase {

    fun update(id: UUID, request: LessonRequest): LessonResponse
}
