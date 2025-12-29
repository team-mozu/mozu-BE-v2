package team.mozu.dsm.adapter.out.organ.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganDetailResponse
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganListResponse
import team.mozu.dsm.adapter.`in`.organ.dto.response.QOrganDetailResponse
import team.mozu.dsm.adapter.`in`.organ.dto.response.QOrganListResponse
import team.mozu.dsm.adapter.out.organ.entity.QOrganJpaEntity.organJpaEntity
import team.mozu.dsm.adapter.out.organ.mapper.OrganMapper
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganJpaRepository
import team.mozu.dsm.application.port.out.organ.OrganPort
import team.mozu.dsm.domain.organ.model.Organ
import java.util.UUID

@Component
class OrganPersistenceAdapter(
    private val organRepository: OrganJpaRepository,
    private val organMapper: OrganMapper,
    private val jpaQueryFactory: JPAQueryFactory
) : OrganPort {

    //--Query--//
    override fun findByOrganCode(organCode: String): Organ? =
        organRepository.findByOrganCode(organCode)?.let { organMapper.toModel(it) }

    override fun findModelById(organId: UUID): Organ? =
        organRepository.findByIdOrNull(organId)
            ?.let { organMapper.toModel(it) }

    override fun findById(id: UUID): OrganDetailResponse? =
        jpaQueryFactory
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

    override fun findOrganInventory(): List<OrganListResponse> =
        jpaQueryFactory
            .select(
                QOrganListResponse(
                    organJpaEntity.id,
                    organJpaEntity.organCode,
                    organJpaEntity.organName,
                    organJpaEntity.password
                )
            )
            .from(organJpaEntity)
            .fetch()

    //--Command--//
    override fun save(organ: Organ): Organ {
        val entity = organMapper.toEntity(organ)
        val savedOrgan = organRepository.save(entity)
        return organMapper.toModel(savedOrgan)
    }
}
