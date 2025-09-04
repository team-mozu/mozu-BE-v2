package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.lesson.CannotEndLessonException
import team.mozu.dsm.application.port.`in`.lesson.EndLessonUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class EndLessonService(
    private val lessonFacade: LessonFacade,
    private val commandLessonPort: CommandLessonPort,
    private val securityPort: SecurityPort
) : EndLessonUseCase {

    @Transactional
    override fun end(id: UUID) {
        val lesson = lessonFacade.findByLessonId(id)
        val organ = securityPort.getCurrentOrgan()

        if (lesson.organId != organ.id) {
            throw CannotEndLessonException
        }

        commandLessonPort.updateIsInProgress(lesson.id!!)
    }
}
