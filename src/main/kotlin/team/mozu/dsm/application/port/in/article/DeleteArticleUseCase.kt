package team.mozu.dsm.application.port.`in`.article

import java.util.UUID

interface DeleteArticleUseCase {

    fun delete(id: UUID)
}
