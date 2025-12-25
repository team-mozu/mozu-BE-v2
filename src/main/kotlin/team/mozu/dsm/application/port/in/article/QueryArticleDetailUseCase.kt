package team.mozu.dsm.application.port.`in`.article

import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import java.util.UUID

interface QueryArticleDetailUseCase {

    fun execute(id: UUID): ArticleResponse
}
