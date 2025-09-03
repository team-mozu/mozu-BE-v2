package team.mozu.dsm.application.port.`in`.article

import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse

interface CreateArticleUseCase {
    fun create(request: ArticleRequest): ArticleResponse
}
