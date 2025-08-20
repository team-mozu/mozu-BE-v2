package team.mozu.dsm.adapter.out.item.persistence.mapper

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.`class`.entity.ClassJpaEntity
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.`class`.model.Class
import team.mozu.dsm.domain.item.model.Item

@Component
class ItemMapper {
    fun toModel(entity: ItemJpaEntity): Item {
        return Item(
            id = entity.id,
            organId = entity.organ.id!!,
            itemName = entity.itemName,
            itemInfo = entity.itemInfo,
            itemLogo = entity.itemLogo,
            isDeleted = entity.isDeleted,
            profit = entity.profit,
            profitOg = entity.profitOg,
            profitBenefit = entity.profitBenefit,
            netProfit = entity.netProfit,
            capital = entity.capital,
            money = entity.money,
            debt = entity.debt,
            createdAt = entity.createdAt
        )
    }

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
