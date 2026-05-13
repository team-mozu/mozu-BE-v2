package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.RoundStatusResponse
import java.util.UUID

interface QueryLessonRoundStatusUseCase {

    fun execute(lessonId: UUID): List<RoundStatusResponse>
}
