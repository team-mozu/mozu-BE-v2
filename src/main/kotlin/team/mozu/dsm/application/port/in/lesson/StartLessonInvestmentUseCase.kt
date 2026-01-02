package team.mozu.dsm.application.port.`in`.lesson

import java.util.UUID

interface StartLessonInvestmentUseCase {

    fun execute(lessonId: UUID)
}
