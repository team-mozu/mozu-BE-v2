package team.mozu.dsm.adapter.`in`.article.dto.response

import java.time.LocalDateTime

data class ArticleQueryResponse (
    val articleName: String,
    val articleDesc: String,
    val createdAt: LocalDateTime,
)
