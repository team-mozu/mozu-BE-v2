package team.mozu.dsm.adapter.out.organ.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganDetailResponse
import team.mozu.dsm.adapter.`in`.organ.dto.response.QOrganDetailResponse
import team.mozu.dsm.adapter.out.organ.entity.QOrganJpaEntity.organJpaEntity
import team.mozu.dsm.adapter.out.organ.persistence.mapper.OrganMapper
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.port.out.organ.CommandOrganPort
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.domain.organ.model.Organ
import java.util.UUID

@Component
class OrganPersistenceAdapter(
    private val organRepository: OrganRepository,
    private val organMapper: OrganMapper,
    private val jpaQueryFactory: JPAQueryFactory
) : QueryOrganPort, CommandOrganPort {

    //--Query--//
    override fun findByOrganCode(organCode: String): Organ? {
        return organRepository.findByOrganCode(organCode)?.let { organMapper.toModel(it) }
    }

    override fun findById(id: UUID): OrganDetailResponse? {
        return jpaQueryFactory
            .select(
                QOrganDetailResponse(
                    organJpaEntity.id,
                    organJpaEntity.organCode,
                    organJpaEntity.organName,
                    organJpaEntity.password
                )
            )
            .from(organJpaEntity)
            .where(organJpaEntity.id.eq(id))
            .fetchOne()
    }

    //--Command--//
    override fun save(organ: Organ): Organ {
        val entity = organMapper.toEntity(organ)
        val savedOrgan = organRepository.save(entity)
        return organMapper.toModel(savedOrgan)
    }
}
