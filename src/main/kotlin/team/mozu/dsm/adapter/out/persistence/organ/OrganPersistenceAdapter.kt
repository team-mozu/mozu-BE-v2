package team.mozu.dsm.adapter.out.persistence.organ

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.persistence.organ.repository.OrganRepository
import team.mozu.dsm.application.port.out.FindOrganPort
import team.mozu.dsm.domain.organ.model.Organ

@Component
class OrganPersistenceAdapter(
    private val OrganRepository: OrganRepository,
    private val organMapper: OrganMapper
) : FindOrganPort {
    override fun findByOrganCode(organCode: String): Organ? {
        return OrganRepository.findByOrganCode(organCode)?.let { organMapper.toModel(it) }
    }
}
