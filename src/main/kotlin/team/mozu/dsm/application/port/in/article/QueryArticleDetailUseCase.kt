package team.mozu.dsm.application.port.`in`.article

import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleDetailResponse
import java.util.UUID

interface QueryArticleDetailUseCase {

    fun execute(id: UUID): ArticleDetailResponse
}
