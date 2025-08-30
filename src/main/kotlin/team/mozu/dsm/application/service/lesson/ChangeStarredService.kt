package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.lesson.ChangeStarredUseCase
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import java.util.UUID

@Service
class ChangeStarredService(
    private val queryLessonPort: QueryLessonPort,
    private val commandLessonPort: CommandLessonPort
) : ChangeStarredUseCase {

    @Transactional
    override fun change(id: UUID) {
        val lesson = queryLessonPort.findById(id)
            ?: throw LessonNotFoundException

        commandLessonPort.updateIsStarred(lesson.id!!)
    }
}
