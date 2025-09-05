package team.mozu.dsm.adapter.out.lesson.entity.id

import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID

@Embeddable
data class LessonArticleId(
    val lessonId: UUID,
    val articleId: UUID
) : Serializable
