package team.mozu.dsm.domain.team.model

import team.mozu.dsm.domain.annotation.Aggregate
import team.mozu.dsm.domain.lesson.model.LessonItemId
import team.mozu.dsm.domain.team.type.OrderType
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class OrderItem(
    val id: UUID?,
    val lessonItemId: LessonItemId,
    val teamId: String,
    val itemName: String,
    val orderType: OrderType,
    val orderCount: Int,
    val itemPrice: Long,
    val totalAmount: Long,
    val invCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
