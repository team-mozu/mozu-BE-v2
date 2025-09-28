package team.mozu.dsm.application.service.organ

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.organ.dto.response.MyOrganResponse
import team.mozu.dsm.application.port.`in`.organ.GetMyOrganUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort

@Service
class GetMyOrganService(
    private val securityPort: SecurityPort
) : GetMyOrganUseCase {

    @Transactional(readOnly = true)
    override fun getMyOrgan(): MyOrganResponse {
        val organ = securityPort.getCurrentOrgan()
        return MyOrganResponse(organId = organ.id!!)
    }
}
