package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.port.`in`.lesson.EndLessonUseCase
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class EndLessonService(
    private val lessonFacade: LessonFacade,
    private val commandLessonPort: CommandLessonPort
) : EndLessonUseCase {

    @Transactional
    override fun end(id: UUID) {
        val lesson = lessonFacade.findByLessonId(id)

        commandLessonPort.updateIsInProgress(lesson.id!!)
    }
}
