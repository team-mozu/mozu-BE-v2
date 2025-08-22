package team.mozu.dsm.adapter.out.item.persistence.mapper

import org.mapstruct.Mapper
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.item.model.Item

@Mapper(componentModel = "spring")
interface ItemMapper {
    fun toModel(entity: ItemJpaEntity): Item
    fun toEntity(model: Item, organ: OrganJpaEntity): ItemJpaEntity
}
