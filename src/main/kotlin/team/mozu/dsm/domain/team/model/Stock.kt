package team.mozu.dsm.domain.team.model

import team.mozu.dsm.domain.annotation.Aggregate
import team.mozu.dsm.domain.lesson.model.LessonItemId
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Stock(
    val id: UUID?,
    val teamId: UUID,
    val lessonItemId: LessonItemId,
    val itemName: String,
    val avgPurchasePrice: Long,
    val quantity: Int,
    val buyMoney: Long,
    val valProfit: Long,
    val profitNum: Double,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
