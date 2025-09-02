package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.adapter.out.item.persistence.mapper.ItemMapper
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.port.`in`.item.QueryItemDetailUseCase
import team.mozu.dsm.application.port.out.item.QueryItemPort
import java.util.*

@Service
class QueryItemDetailService (
    private val queryItemPort: QueryItemPort,
    private val itemMapper: ItemMapper
) : QueryItemDetailUseCase {

    @Transactional
    override fun queryDetail(id: UUID): ItemResponse {
        val item = queryItemPort.findById(id)
            ?: throw ItemNotFoundException

        return itemMapper.toResponse(item)
    }
}
