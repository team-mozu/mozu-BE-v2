package team.mozu.dsm.adapter.out.item.persistence.mapper

import org.mapstruct.Mapper
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.item.model.Item

@Mapper(componentModel = "spring")
abstract class ItemMapper {
    abstract fun toModel(entity: ItemJpaEntity): Item

    fun toEntity(model: Item, organ: OrganJpaEntity): ItemJpaEntity {
        return ItemJpaEntity(
            organ = organ,
            itemName = model.itemName,
            itemLogo = model.itemLogo,
            itemInfo = model.itemInfo,
            isDeleted = model.isDeleted,
            capital = model.capital,
            debt = model.debt,
            profit = model.profit,
            profitOg = model.profitOg,
            profitBenefit = model.profitBenefit,
            netProfit = model.netProfit,
            money = model.money
        )
    }
}
