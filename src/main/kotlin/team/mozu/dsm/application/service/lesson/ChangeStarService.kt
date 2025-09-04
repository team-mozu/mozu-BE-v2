package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.lesson.CannotStarLessonException
import team.mozu.dsm.application.port.`in`.lesson.ChangeStarUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class ChangeStarService(
    private val lessonFacade: LessonFacade,
    private val commandLessonPort: CommandLessonPort,
    private val securityPort: SecurityPort
) : ChangeStarUseCase {

    @Transactional
    override fun change(id: UUID) {
        val lesson = lessonFacade.findByLessonId(id)
        val organ = securityPort.getCurrentOrgan()

        if (lesson.organId != organ.id) {
            throw CannotStarLessonException
        }

        commandLessonPort.updateIsStarred(lesson.id!!)
    }
}
