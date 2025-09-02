package team.mozu.dsm.application.port.`in`.lesson

import java.util.UUID

interface DeleteLessonUseCase {

    fun delete(id: UUID)
}
