package team.mozu.dsm.adapter.`in`.team.dto.response

import team.mozu.dsm.domain.team.type.OrderType
import java.util.UUID

data class OrderItemResponse(
    val id: UUID,
    val itemId: UUID,
    val itemName: String,
    val itemPrice: Int,
    val orderCount: Int,
    val totalMoney: Int,
    val orderType: OrderType,
    val invCount: Int
)
