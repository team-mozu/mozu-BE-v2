package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.application.port.`in`.item.QueryItemsUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.item.QueryItemPort

@Service
class QueryItemsService(
    private val queryItemPort: QueryItemPort,
    private val securityPort: SecurityPort
) : QueryItemsUseCase {

    @Transactional(readOnly = true)
    override fun execute(): List<ItemResponse> =
        queryItemPort.findAllByOrganId(securityPort.getCurrentOrgan().id!!)
            .map { ItemResponse.from(it) }
}
