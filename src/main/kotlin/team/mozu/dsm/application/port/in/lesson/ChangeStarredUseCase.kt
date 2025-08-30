package team.mozu.dsm.application.port.`in`.lesson

import java.util.UUID

interface ChangeStarredUseCase {

    fun change(id: UUID)
}
