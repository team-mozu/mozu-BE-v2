package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.item.dto.request.UpdateItemRequest
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
    override fun update(id: Int, request: UpdateItemRequest): ItemResponse {
        val organ = securityPort.getCurrentOrgan()
        val item = queryItemPort.findById(id) ?: throw ItemNotFoundException

        if (item.organId != organ.id) {
            throw CannotDeleteLessonException
        }

        val newLogoUrl: String? = when {
            // 1. 명시적 삭제: deleteLogo = true 또는 itemLogoUrl = ""
            request.deleteLogo || request.itemLogoUrl == "" -> {
                // 기존 로고가 있다면 S3에서 삭제
                item.itemLogo?.let { s3Port.delete(it) }
                null
            }
            // 2. 새 로고 업로드: MultipartFile 객체 제공
            request.itemLogo != null && !request.itemLogo.isEmpty -> {
                // 기존 로고가 있다면 S3에서 삭제
                item.itemLogo?.let { s3Port.delete(it) }
                s3Port.upload(request.itemLogo)
            }
            // 3. URL로 로고 교체: itemLogoUrl에 새 URL 제공
            !request.itemLogoUrl.isNullOrBlank() -> {
                // 기존 로고가 있다면 S3에서 삭제
                item.itemLogo?.let { s3Port.delete(it) }
                request.itemLogoUrl
            }
            // 4. 기존 로고 유지: null (변경 없음)
            else -> item.itemLogo
        }

        val updated = item.copy(
            itemName = request.itemName,
            itemInfo = request.itemInfo,
            itemLogo = newLogoUrl,
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
