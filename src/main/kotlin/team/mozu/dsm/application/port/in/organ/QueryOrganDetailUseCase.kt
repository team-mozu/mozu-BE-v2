package team.mozu.dsm.application.port.`in`.organ

import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganDetailResponse
import java.util.*

interface QueryOrganDetailUseCase {

    fun queryOrganDetail(id: UUID): OrganDetailResponse
}
