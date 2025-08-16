package team.mozu.dsm.adapter.out.persistence.organ
import org.mapstruct.Mapper
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.organ.model.Organ

@Mapper(componentModel = "spring")
interface OrganMapper {
    fun toModel(entity: OrganJpaEntity): Organ
    fun toEntity(model: Organ): OrganJpaEntity
}
