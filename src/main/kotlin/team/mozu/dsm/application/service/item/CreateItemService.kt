package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.item.dto.request.ItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.adapter.out.item.persistence.mapper.ItemMapper
import team.mozu.dsm.application.port.`in`.item.CreateItemUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.item.CommandItemPort
import team.mozu.dsm.domain.item.model.Item
import java.time.LocalDateTime

@Service
class CreateItemService(
    private val securityPort: SecurityPort,
    private val itemPort: CommandItemPort,
    private val itemMapper: ItemMapper
) : CreateItemUseCase {

    @Transactional
    override fun create(request: ItemRequest): ItemResponse {
        val organ = securityPort.getCurrentOrgan()

        val logoUrl: String? = request.itemLogo
            ?.trim()
            ?.takeUnless { it.isBlank() }

        val item = Item(
            id = null,
            organId = organ.id!!,
            itemName = request.itemName,
            itemLogo = logoUrl,
            itemInfo = request.itemInfo,
            money = request.money,
            debt = request.debt,
            capital = request.capital,
            profit = request.profit,
            profitOg = request.profitOg,
            profitBenefit = request.profitBenefit,
            netProfit = request.netProfit,
            isDeleted = false,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val saved = itemPort.save(item)

        return itemMapper.toResponse(saved)
    }
}
