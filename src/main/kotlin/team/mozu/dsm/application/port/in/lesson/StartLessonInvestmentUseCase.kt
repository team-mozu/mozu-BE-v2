package team.mozu.dsm.application.port.`in`.lesson

import java.util.UUID

interface StartLessonInvestmentUseCase {
    fun startInvestment(lessonId: UUID)
}