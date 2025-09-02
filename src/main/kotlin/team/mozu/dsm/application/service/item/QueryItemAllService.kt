package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.adapter.out.item.persistence.mapper.ItemMapper
import team.mozu.dsm.application.port.`in`.item.QueryItemAllUseCase
import team.mozu.dsm.application.port.out.item.QueryItemPort

@Service
class QueryItemAllService (
    private val queryItemPort: QueryItemPort,
    private val itemMapper: ItemMapper
) : QueryItemAllUseCase{

    @Transactional
    override fun queryAll(): List<ItemResponse> {
        return queryItemPort.findAll().map {itemMapper.toResponse(it)}
    }
}
