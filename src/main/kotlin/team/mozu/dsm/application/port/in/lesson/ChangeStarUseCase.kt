package team.mozu.dsm.application.port.`in`.lesson

import java.util.UUID

interface ChangeStarUseCase {

    fun execute(id: UUID)
}
