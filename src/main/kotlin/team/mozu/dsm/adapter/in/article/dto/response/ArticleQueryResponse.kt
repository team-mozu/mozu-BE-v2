package team.mozu.dsm.adapter.`in`.article.dto.response

import java.time.LocalDateTime
import java.util.UUID

data class ArticleQueryResponse (
    val id: UUID,
    val articleName: String,
    val createdAt: LocalDateTime,
)
