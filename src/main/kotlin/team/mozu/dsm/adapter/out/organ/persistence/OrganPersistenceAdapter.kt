package team.mozu.dsm.adapter.out.organ.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganDetailResponse
import team.mozu.dsm.adapter.`in`.organ.dto.response.QOrganDetailResponse
import team.mozu.dsm.adapter.out.organ.entity.QOrganJpaEntity
import team.mozu.dsm.adapter.out.organ.persistence.mapper.OrganMapper
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.application.port.out.organ.CommandOrganPort
import team.mozu.dsm.domain.organ.model.Organ
import java.util.UUID

@Component
class OrganPersistenceAdapter(
    private val organRepository: OrganRepository,
    private val organMapper: OrganMapper,
    private val jpaQueryFactory: JPAQueryFactory
) : QueryOrganPort, CommandOrganPort {

    private val organ = QOrganJpaEntity.organJpaEntity

    override fun findByOrganCode(organCode: String): Organ? {
        return organRepository.findByOrganCode(organCode)?.let { organMapper.toModel(it) }
    }

    override fun save(organ: Organ): Organ {
        val organ = organMapper.toEntity(organ)
        val savedOrgan = organRepository.save(organ)
        return organMapper.toModel(savedOrgan)
    }

    override fun findById(id: UUID): OrganDetailResponse? {
        return jpaQueryFactory
            .select(
                QOrganDetailResponse(
                    organ.id,
                    organ.organCode,
                    organ.organName,
                    organ.password
                )
            )
            .from(organ)
            .where(organ.id.eq(id))
            .fetchOne()
    }
}
