package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.item.dto.request.ItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.application.port.`in`.item.CreateItemUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.item.CommandItemPort
import team.mozu.dsm.domain.item.model.Item
import java.time.LocalDateTime
import java.util.*

@Service
class CreateItemService (
    private val securityPort: SecurityPort,
    private val itemPort: CommandItemPort
) : CreateItemUseCase {

    @Transactional
    override fun create(request: ItemRequest): ItemResponse {
        val organ = securityPort.getCurrentOrgan()

        val item = Item(
            id = UUID.randomUUID(),
            organId = organ.id!!,
            itemName = request.itemName,
            itemLogo = null,
            itemInfo = request.itemInfo,
            money = request.money,
            debt = request.debt,
            capital = request.capital,
            profit = request.profit,
            profitOg  = request.profitOg ,
            profitBenefit  = request.profitBenefit ,
            netProfit = request.netProfit,
            isDeleted = false,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val saved = itemPort.save(item)

        return ItemResponse(
            id = saved.id!!,
            name = saved.itemName,
            logo = saved.itemLogo,
            info = saved.itemInfo,
            money = saved.money,
            debt = saved.debt,
            capital = saved.capital,
            profit = saved.profit,
            profitOg = saved.profitOg,
            profitBenefit = saved.profitBenefit,
            netProfit = saved.netProfit,
            isDeleted = saved.isDeleted,
            createdAt = saved.createdAt
        )
    }
}
