package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.OrderItem

interface OrderItemCommandPort {

    fun saveAll(orderItem: List<OrderItem>)
}
