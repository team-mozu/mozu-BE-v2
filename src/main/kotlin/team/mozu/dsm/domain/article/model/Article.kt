package team.mozu.dsm.domain.article.model

import team.mozu.dsm.adapter.`in`.article.dto.request.UpdateArticleRequest
import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.*

@Aggregate
data class Article(
    val id: UUID? = null,
    val organId: UUID,
    val articleName: String,
    val articleImage: String?,
    val articleDesc: String,
    val isDeleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    fun updateArticle(request: UpdateArticleRequest, articleImage: String?): Article {
        return copy(
            articleName = request.articleName,
            articleDesc = request.articleDesc,
            articleImage = articleImage,
            updatedAt = LocalDateTime.now()
        )
    }
}
