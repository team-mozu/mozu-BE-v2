package team.mozu.dsm.application.port.`in`.article

import java.util.*

interface DeleteArticleUseCase {

    fun delete(id: UUID)
}
