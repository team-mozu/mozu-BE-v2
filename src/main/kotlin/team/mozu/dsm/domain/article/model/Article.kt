package team.mozu.dsm.domain.article.model

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
)
