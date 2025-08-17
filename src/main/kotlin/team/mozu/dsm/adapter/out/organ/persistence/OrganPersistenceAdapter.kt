package team.mozu.dsm.adapter.out.organ.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.organ.persistence.mapper.OrganMapper
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.domain.organ.model.Organ

@Component
class OrganPersistenceAdapter(
    private val organRepository: OrganRepository,
    private val organMapper: OrganMapper
) : QueryOrganPort {

    override fun findByOrganCode(organCode: String): Organ? {
        return organRepository.findByOrganCode(organCode)?.let { organMapper.toModel(it) }
    }
}
