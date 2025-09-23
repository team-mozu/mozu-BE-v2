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
import team.mozu.dsm.application.port.out.s3.S3Port
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateItemService(
    private val commandItemPort: CommandItemPort,
    private val queryItemPort: QueryItemPort,
    private val itemMapper: ItemMapper,
    private val securityPort: SecurityPort,
    private val s3Port: S3Port
) : UpdateItemUseCase {

    @Transactional
    override fun update(id: Int, request: ItemRequest): ItemResponse {
        val organ = securityPort.getCurrentOrgan()
        val item = queryItemPort.findById(id) ?: throw ItemNotFoundException

        if (item.organId != organ.id) {
            throw CannotDeleteLessonException
        }

        val newLogoUrl: String? = request.itemLogo
            ?.takeIf { !it.isEmpty }      // 파일이 있을 때만
            ?.let { s3Port.upload(it) }    // S3에 업로드하고 URL 반환

        val updated = item.copy(
            itemName = request.itemName,
            itemInfo = request.itemInfo,
            itemLogo = newLogoUrl ?: item.itemLogo,
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
