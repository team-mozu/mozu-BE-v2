package team.mozu.dsm.application.port.`in`.organ

import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganListResponse

interface QueryOrganInventoryUseCase {

    fun queryOrganInventory(): List<OrganListResponse>
}
