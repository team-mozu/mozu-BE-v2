package team.mozu.dsm.application.port.`in`.article

import team.mozu.dsm.adapter.`in`.article.dto.request.UpdateArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import java.util.UUID

interface UpdateArticleUseCase {

    fun execute(id: UUID, request: UpdateArticleRequest): ArticleResponse
}
