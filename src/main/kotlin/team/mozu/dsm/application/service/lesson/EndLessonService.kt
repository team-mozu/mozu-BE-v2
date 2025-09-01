package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.lesson.EndLessonUseCase
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import java.util.UUID

@Service
class EndLessonService(
    private val queryLessonPort: QueryLessonPort,
    private val commandLessonPort: CommandLessonPort
) : EndLessonUseCase {

    @Transactional
    override fun end(id: UUID) {
        val lesson = queryLessonPort.findById(id)
            ?: throw LessonNotFoundException

        commandLessonPort.updateIsInProgress(lesson.id!!)
    }
}
