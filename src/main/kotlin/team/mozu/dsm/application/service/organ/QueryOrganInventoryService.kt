package team.mozu.dsm.application.service.organ

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganListResponse
import team.mozu.dsm.application.port.`in`.organ.QueryOrganInventoryUseCase
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.application.service.organ.facade.OrganFacade

@Service
class QueryOrganInventoryService(
    private val queryOrganPort: QueryOrganPort,
    private val organFacade: OrganFacade
) : QueryOrganInventoryUseCase {

    override fun queryOrganInventory(): List<OrganListResponse> {
        organFacade.currentOrgan()

        return queryOrganPort.findOrganInventory()
    }
}
