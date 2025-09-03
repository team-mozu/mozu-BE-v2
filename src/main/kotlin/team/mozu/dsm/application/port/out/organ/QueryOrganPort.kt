package team.mozu.dsm.application.port.out.organ

import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganDetailResponse
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganListResponse
import team.mozu.dsm.domain.organ.model.Organ
import java.util.UUID

interface QueryOrganPort {

    fun findByOrganCode(organCode: String): Organ?

    fun findById(id: UUID): OrganDetailResponse?

    fun findOrganInventory(): List<OrganListResponse>

    fun findModelById(organId: UUID): Organ?
}
