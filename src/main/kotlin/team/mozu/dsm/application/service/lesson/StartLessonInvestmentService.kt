package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.lesson.CannotStartLessonException
import team.mozu.dsm.application.port.`in`.lesson.StartLessonInvestmentUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import team.mozu.dsm.application.service.team.facade.TeamFacade
import team.mozu.dsm.domain.sse.model.SseEventType
import java.util.UUID

@Service
class StartLessonInvestmentService(
    private val lessonFacade: LessonFacade,
    private val teamFacade: TeamFacade,
    private val securityPort: SecurityPort
) : StartLessonInvestmentUseCase {

    @Transactional
    override fun startInvestment(lessonId: UUID) {
        val organ = securityPort.getCurrentOrgan()
        val lesson = lessonFacade.findByLessonId(lessonId)

        if (lesson.organId != organ.id) {
            throw CannotStartLessonException
        }

        teamFacade.updateCurRoundAndPublishEvent(lesson.id!!, SseEventType.START_LESSON_EVENT)
    }
}
