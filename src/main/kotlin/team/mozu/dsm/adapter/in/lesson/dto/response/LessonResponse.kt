package team.mozu.dsm.adapter.`in`.lesson.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import team.mozu.dsm.domain.article.model.Article
import team.mozu.dsm.domain.item.model.Item
import team.mozu.dsm.domain.lesson.model.Lesson
import team.mozu.dsm.domain.lesson.model.LessonItem
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
    val lessonItems: List<LessonItemResponse>,
    val lessonArticles: List<LessonArticleResponse>
) {
    companion object {
        fun of(
            lesson: Lesson,
            lessonItems: List<LessonItemResponse>,
            lessonArticles: List<LessonArticleResponse>
        ): LessonResponse {
            return LessonResponse(
                id = lesson.id!!,
                name = lesson.lessonName,
                maxInvRound = lesson.maxInvRound,
                curInvRound = lesson.curInvRound,
                baseMoney = lesson.baseMoney,
                lessonNum = lesson.lessonNum,
                isInProgress = lesson.isInProgress,
                isStarred = lesson.isStarred,
                isDeleted = lesson.isDeleted,
                createdAt = lesson.createdAt!!,
                lessonItems = lessonItems,
                lessonArticles = lessonArticles
            )
        }
    }
}

data class LessonItemResponse(
    val itemId: Int,
    val itemName: String,
    val money: List<Long>
) {
    companion object {
        fun of(item: Item, lessonItem: LessonItem): LessonItemResponse {
            return LessonItemResponse(
                itemId = item.id!!,
                itemName = item.itemName,
                money = listOf(
                    lessonItem.round1Price,
                    lessonItem.round2Price,
                    lessonItem.round3Price
                ) + listOfNotNull(lessonItem.round4Price, lessonItem.round5Price, lessonItem.endPrice)
            )
        }
    }
}

data class LessonArticleResponse(
    val investmentRound: Int,
    val articles: List<ArticleResponse>
)

data class ArticleResponse(
    val articleId: UUID,
    val title: String
) {
    companion object {
        fun from(article: Article): ArticleResponse {
            return ArticleResponse(
                articleId = article.id!!,
                title = article.articleName
            )
        }
    }
}
