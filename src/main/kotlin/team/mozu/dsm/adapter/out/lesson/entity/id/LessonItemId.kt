package team.mozu.dsm.adapter.out.lesson.entity.id

import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID

@Embeddable
data class LessonItemId(
    var lessonId: UUID,
    var itemId: UUID
) : Serializable
