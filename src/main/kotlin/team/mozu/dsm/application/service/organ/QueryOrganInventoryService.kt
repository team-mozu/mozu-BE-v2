package team.mozu.dsm.application.service.organ

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganListResponse
import team.mozu.dsm.application.port.`in`.organ.QueryOrganInventoryUseCase
import team.mozu.dsm.application.port.out.organ.QueryOrganPort

@Service
class QueryOrganInventoryService(
    private val queryOrganPort: QueryOrganPort
) : QueryOrganInventoryUseCase {

    override fun queryOrganInventory(): List<OrganListResponse> {

        return queryOrganPort.findOrganInventory()
    }
}
