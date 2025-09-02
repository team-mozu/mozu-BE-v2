package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.port.`in`.lesson.DeleteLessonUseCase
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class DeleteLessonService(
    private val lessonFacade: LessonFacade,
    private val commandLessonPort: CommandLessonPort
) : DeleteLessonUseCase {

    @Transactional
    override fun delete(id: UUID) {
        val lesson = lessonFacade.findByLessonId(id)

        commandLessonPort.delete(lesson.id!!)
    }
}
