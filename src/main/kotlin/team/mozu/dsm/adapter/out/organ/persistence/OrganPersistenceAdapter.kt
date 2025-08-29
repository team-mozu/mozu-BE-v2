package team.mozu.dsm.adapter.out.organ.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.organ.persistence.mapper.OrganMapper
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.application.port.out.organ.CommandOrganPort
import team.mozu.dsm.domain.organ.model.Organ
import java.util.*

@Component
class OrganPersistenceAdapter(
    private val organRepository: OrganRepository,
    private val organMapper: OrganMapper
) : QueryOrganPort, CommandOrganPort {

    override fun findByOrganCode(organCode: String): Organ? {
        return organRepository.findByOrganCode(organCode)?.let { organMapper.toModel(it) }
    }

    override fun findById(organId: UUID): Organ? {
        return organRepository.findByIdOrNull(organId)
            ?.let { organMapper.toModel(it) }
    }

    override fun save(organ: Organ): Organ {
        val organ = organMapper.toEntity(organ)
        val savedOrgan = organRepository.save(organ)
        return organMapper.toModel(savedOrgan)
    }
}
