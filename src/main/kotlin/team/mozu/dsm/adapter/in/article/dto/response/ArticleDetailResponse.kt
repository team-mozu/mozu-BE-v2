package team.mozu.dsm.adapter.`in`.article.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import team.mozu.dsm.domain.article.model.Article
import java.time.LocalDateTime
import java.util.UUID

data class ArticleDetailResponse(
    val id: UUID,
    val articleName: String,
    val articleDesc: String,
    val articleImg: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val createdAt: LocalDateTime,
    val isDeleted: Boolean
) {
    companion object {
        fun from(article: Article): ArticleDetailResponse {
            return ArticleDetailResponse(
                id = article.id!!,
                articleName = article.articleName,
                articleDesc = article.articleDesc,
                articleImg = article.articleImage,
                createdAt = article.createdAt,
                isDeleted = article.isDeleted
            )
        }
    }
}
