package team.mozu.dsm.application.service.organ

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganDetailResponse
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.`in`.organ.QueryOrganDetailUseCase
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import java.util.*

@Service
class QueryOrganDetailService(
    private val queryOrganPort: QueryOrganPort
) : QueryOrganDetailUseCase {

    override fun queryOrganDetail(id: UUID): OrganDetailResponse {
        return queryOrganPort.findById(id) ?: throw OrganNotFoundException
    }
}
