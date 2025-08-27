package team.mozu.dsm.global.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.organ.persistence.mapper.OrganMapper
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.domain.organ.model.Organ

@Component
class SecurityAdapter(
    private val organRepository: OrganRepository,
    private val organMapper: OrganMapper
) : SecurityPort {

    override fun getCurrentOrgan(): Organ {
        val organCode = SecurityContextHolder.getContext().authentication.name
        val entity =  organRepository.findByOrganCode(organCode)
            ?: throw OrganNotFoundException

        return organMapper.toModel(entity)
    }
}
