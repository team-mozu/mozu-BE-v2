package team.mozu.dsm.application.port.`in`.item

import java.util.*

interface DeleteItemUseCase {
    fun delete(id : UUID)
}
