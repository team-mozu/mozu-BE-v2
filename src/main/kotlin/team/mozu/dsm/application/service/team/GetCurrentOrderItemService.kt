package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.OrderItemResponse
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetCurrentOrderItemUseCase
import team.mozu.dsm.application.port.out.team.QueryOrderItemPort
import java.util.*

@Service
class GetCurrentOrderItemService(
    private val queryOrderItemPort: QueryOrderItemPort
) : GetCurrentOrderItemUseCase {

    @Transactional(readOnly = true)
    override fun getCurrentOrderItem(teamId: UUID): OrderItemResponse {
        return queryOrderItemPort.findByTeamId(teamId) ?: throw TeamNotFoundException
    }
}
