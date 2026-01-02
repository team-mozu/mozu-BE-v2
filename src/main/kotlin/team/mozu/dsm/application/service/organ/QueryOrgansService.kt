package team.mozu.dsm.application.service.organ

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganListResponse
import team.mozu.dsm.application.port.`in`.organ.QueryOrgansUseCase
import team.mozu.dsm.application.port.out.organ.QueryOrganPort

@Service
class QueryOrgansService(
    private val queryOrganPort: QueryOrganPort
) : QueryOrgansUseCase {

    override fun execute(): List<OrganListResponse> =
        queryOrganPort.findAll()
}
