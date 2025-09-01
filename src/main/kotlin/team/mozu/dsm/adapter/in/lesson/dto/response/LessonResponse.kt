package team.mozu.dsm.adapter.`in`.lesson.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.UUID

data class LessonResponse(
    val id: UUID,
    val name: String,
    val maxInvRound: Int,
    val curInvRound: Int? = null,
    val baseMoney: Long,
    val lessonNum: String? = null,
    val isInProgress: Boolean,
    val isStarred: Boolean,
    val isDeleted: Boolean,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val createdAt: LocalDateTime,
    val lessonArticles: List<LessonArticleResponse>,
    val lessonItems: List<LessonItemResponse>
)

data class LessonArticleResponse(
    val investmentRound: Int,
    val articles: List<ArticleResponse>
)

data class ArticleResponse(
    val articleId: UUID,
    val title: String
)

data class LessonItemResponse(
    val itemId: UUID,
    val itemName: String,
    val money: List<Int>
)
