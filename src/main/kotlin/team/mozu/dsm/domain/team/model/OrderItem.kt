package team.mozu.dsm.domain.team.model

import team.mozu.dsm.adapter.out.lesson.entity.id.LessonItemId
import team.mozu.dsm.domain.annotation.Aggregate
import team.mozu.dsm.domain.lesson.model.LessonItem
import team.mozu.dsm.domain.team.type.OrderType
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class OrderItem(
    val id: UUID?,
    val lessonItemId: LessonItemId,
    val lessonItem: LessonItem,
    val teamId: String,
    val orderType: OrderType,
    val orderCount: Int,
    val itemPrice: Long,
    val totalAmount: Long,
    val orderedAt: LocalDateTime,
    val invCnt: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
