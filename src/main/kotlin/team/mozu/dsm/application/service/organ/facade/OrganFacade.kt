package team.mozu.dsm.application.service.organ.facade

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.domain.organ.model.Organ

@Component
class OrganFacade(
    private val queryOrganPort: QueryOrganPort
) {

    fun currentOrgan(): Organ {
        val organCode: String = SecurityContextHolder.getContext().authentication.name

        return queryOrganPort.findByOrganCode(organCode)
            ?: throw OrganNotFoundException
    }
}
