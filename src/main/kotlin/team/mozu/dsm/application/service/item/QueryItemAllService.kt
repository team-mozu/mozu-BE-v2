package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemQueryResponse
import team.mozu.dsm.adapter.out.item.mapper.ItemMapper
import team.mozu.dsm.application.port.`in`.item.QueryItemAllUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.item.QueryItemPort

@Service
class QueryItemAllService(
    private val queryItemPort: QueryItemPort,
    private val itemMapper: ItemMapper,
    private val securityPort: SecurityPort
) : QueryItemAllUseCase {

    @Transactional(readOnly = true)
    override fun execute(): List<ItemQueryResponse> {
        val currentOrgan = securityPort.getCurrentOrgan()
        return queryItemPort.findAllByOrganId(currentOrgan.id!!).map { itemMapper.toQueryResponse(it) }
    }
}
