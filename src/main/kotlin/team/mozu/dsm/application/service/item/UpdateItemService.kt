package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.item.dto.request.ItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.adapter.out.item.persistence.mapper.ItemMapper
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.lesson.CannotDeleteLessonException
import team.mozu.dsm.application.port.`in`.item.UpdateItemUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.item.CommandItemPort
import team.mozu.dsm.application.port.out.item.QueryItemPort
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateItemService(
    private val commandItemPort: CommandItemPort,
    private val queryItemPort: QueryItemPort,
    private val itemMapper: ItemMapper,
    private val securityPort: SecurityPort
) : UpdateItemUseCase {

    @Transactional
    override fun update(id: Int, request: ItemRequest): ItemResponse {
        val organ = securityPort.getCurrentOrgan()
        val item = queryItemPort.findById(id) ?: throw ItemNotFoundException

        if (item.organId != organ.id) {
            throw CannotDeleteLessonException
        }

        val updated = item.copy(
            itemName = request.itemName,
            itemInfo = request.itemInfo,
            itemLogo = request.itemLogo,
            money = request.money,
            debt = request.debt,
            capital = request.capital,
            profit = request.profit,
            profitOg = request.profitOg,
            profitBenefit = request.profitBenefit,
            netProfit = request.netProfit,
            updatedAt = LocalDateTime.now()
        )

        val saved = commandItemPort.save(updated)
        return itemMapper.toResponse(saved)
    }
}
