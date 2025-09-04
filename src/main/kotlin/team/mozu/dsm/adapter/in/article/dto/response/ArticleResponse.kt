package team.mozu.dsm.adapter.`in`.article.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.UUID

data class ArticleResponse(
    val id: UUID,
    val articleName: String,
    val articleDesc: String,
    val articleImg: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val createdAt: LocalDateTime,
    val isDeleted: Boolean
)
