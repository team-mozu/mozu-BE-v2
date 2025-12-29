package team.mozu.dsm.application.service.organ

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.organ.dto.response.MyOrganResponse
import team.mozu.dsm.application.port.`in`.organ.QueryMyOrganUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort

@Service
class QueryMyOrganService(
    private val securityPort: SecurityPort
) : QueryMyOrganUseCase {

    @Transactional(readOnly = true)
    override fun execute(): MyOrganResponse =
        MyOrganResponse(securityPort.getCurrentOrgan().id!!)
}
