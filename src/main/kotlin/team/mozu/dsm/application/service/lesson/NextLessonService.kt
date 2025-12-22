package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.lesson.CannotNextLessonException
import team.mozu.dsm.application.exception.lesson.LessonNotInProgressException
import team.mozu.dsm.application.port.`in`.lesson.NextLessonUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import team.mozu.dsm.application.service.team.facade.TeamFacade
import team.mozu.dsm.domain.sse.model.SseEventType
import java.util.UUID

@Service
class NextLessonService(
    private val lessonFacade: LessonFacade,
    private val teamFacade: TeamFacade,
    private val securityPort: SecurityPort
) : NextLessonUseCase {

    @Transactional
    override fun next(lessonId: UUID) {
        val organ = securityPort.getCurrentOrgan()
        val lesson = lessonFacade.findByLessonId(lessonId)

        if (lesson.organId != organ.id) {
            throw CannotNextLessonException
        }

        if (!lesson.isInProgress) {
            throw LessonNotInProgressException
        }

        teamFacade.updateCurRoundAndPublishEvent(lessonId, SseEventType.NEXT_LESSON_EVENT)
    }
}
