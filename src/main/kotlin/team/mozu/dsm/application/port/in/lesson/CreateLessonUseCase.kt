package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse

interface CreateLessonUseCase {

    fun create(request: LessonRequest): LessonResponse
}
