package team.mozu.dsm.application.port.`in`.lesson

import java.util.UUID

interface EndLessonUseCase {

    fun end(id: UUID)
}
