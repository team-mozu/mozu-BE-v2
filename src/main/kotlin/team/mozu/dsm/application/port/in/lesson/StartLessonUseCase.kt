package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.StartLessonResponse
import java.util.UUID

interface StartLessonUseCase {

    fun start(id: UUID) : StartLessonResponse
}
