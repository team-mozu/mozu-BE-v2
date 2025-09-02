package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.team.dto.response.OrderItemResponse
import team.mozu.dsm.application.port.`in`.team.GetOrderItemUseCase
import team.mozu.dsm.application.port.out.team.QueryOrderItemPort
import java.util.UUID

@Service
class GetOrderItemService(
    private val queryOrderItemPort: QueryOrderItemPort
) : GetOrderItemUseCase {

    override fun getOrderItems(teamId: UUID): List<OrderItemResponse> {
        val orderItems = queryOrderItemPort.findAllByTeamId(teamId)

        return orderItems.map { orderItem ->
            OrderItemResponse(
                id = orderItem.id!!,
                itemId = orderItem.itemId,
                itemName = orderItem.itemName,
                itemPrice = orderItem.itemPrice,
                orderCount = orderItem.orderCount,
                totalMoney = orderItem.totalMoney,
                orderType = orderItem.orderType,
                invCount = orderItem.invCount
            )
        }
    }
}
