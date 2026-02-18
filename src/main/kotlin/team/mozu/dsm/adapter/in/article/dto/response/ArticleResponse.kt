package team.mozu.dsm.adapter.`in`.article.dto.response

import team.mozu.dsm.domain.article.model.Article
import java.time.LocalDateTime
import java.util.UUID

data class ArticleResponse(
    val id: UUID,
    val articleName: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(article: Article): ArticleResponse {
            return ArticleResponse(
                id = article.id!!,
                articleName = article.articleName,
                createdAt = article.createdAt
            )
        }
    }
}
