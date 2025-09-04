package team.mozu.dsm.adapter.`in`.lesson.dto.response

import com.querydsl.core.annotations.QueryProjection
import java.util.UUID

data class LessonRoundArticleResponse @QueryProjection constructor(
    val articleId: UUID,
    val title: String,
    val description: String,
    val image: String?,
)
