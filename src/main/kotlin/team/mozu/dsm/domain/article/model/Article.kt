package team.mozu.dsm.domain.article.model

import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Article(
    val id: UUID?,
    val organId: UUID,
    val articleName: String,
    val articleImage: String?,
    val articleDesc: String,
    val isDeleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    fun updateArticle(request: ArticleRequest, articleImage: String?): Article {
        return copy(
            articleName = request.articleName,
            articleDesc = request.articleDesc,
            articleImage = articleImage,
            updatedAt = LocalDateTime.now()
        )
    }
}
