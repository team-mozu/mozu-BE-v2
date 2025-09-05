package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.application.port.`in`.lesson.command.UpdateLessonCommand
import team.mozu.dsm.domain.lesson.model.Lesson
import java.util.UUID

interface CommandLessonPort {

    fun save(lesson: Lesson): Lesson

    fun updateLessonNumAndIsInProgress(id: UUID, lessonNum: String)

    fun updateIsStarred(id: UUID)

    fun delete(id: UUID)

    fun updateIsInProgress(id: UUID)

    fun update(lessonId: UUID, command: UpdateLessonCommand)

    fun updateCurInvRound(id: UUID): Lesson
}
