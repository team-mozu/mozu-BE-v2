package team.mozu.dsm.adapter.out.item.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.item.model.Item

@Mapper(componentModel = "spring")
interface ItemMapper {

    @Mapping(target = "organId", source = "entity.organ.id")
    fun toModel(entity: ItemJpaEntity): Item

    fun toEntity(model: Item, organ: OrganJpaEntity): ItemJpaEntity {
        return ItemJpaEntity(
            organ = organ,
            itemName = model.itemName,
            itemLogo = model.itemLogo,
            itemInfo = model.itemInfo,
            money = model.money,
            debt = model.debt,
            capital = model.capital,
            profit = model.profit,
            profitOg = model.profitOg,
            profitBenefit = model.profitBenefit,
            netProfit = model.netProfit,
            isDeleted = model.isDeleted
        )
    }
}
