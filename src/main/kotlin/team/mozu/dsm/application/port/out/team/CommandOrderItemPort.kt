package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.OrderItem

interface CommandOrderItemPort {

    fun saveAll(orderItem: List<OrderItem>)
}
