package team.mozu.dsm.application.port.`in`.lesson

import java.util.UUID

interface NextLessonUseCase {

    fun next(lessonId: UUID)
}
