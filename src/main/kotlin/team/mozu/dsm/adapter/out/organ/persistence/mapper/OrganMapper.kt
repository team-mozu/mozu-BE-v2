package team.mozu.dsm.adapter.out.organ.persistence.mapper

import org.mapstruct.Mapper
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.organ.model.Organ

@Mapper(componentModel = "spring")
abstract class OrganMapper {

    abstract fun toModel(entity: OrganJpaEntity): Organ

    fun toEntity(model: Organ): OrganJpaEntity {
        return OrganJpaEntity(
            organCode = model.organCode,
            organName = model.organName,
            password = model.password
        )
    }
}
