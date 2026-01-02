package team.mozu.dsm.application.port.`in`.article

import team.mozu.dsm.adapter.`in`.article.dto.request.UpdateArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleDetailResponse
import java.util.UUID

interface UpdateArticleUseCase {

    fun execute(id: UUID, request: UpdateArticleRequest): ArticleDetailResponse
}
