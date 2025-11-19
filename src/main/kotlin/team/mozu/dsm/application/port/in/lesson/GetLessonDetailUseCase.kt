package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import java.util.UUID

interface GetLessonDetailUseCase {

    fun get(id: UUID): LessonResponse
}
