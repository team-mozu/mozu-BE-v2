package team.mozu.dsm.adapter.out.item.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemQueryResponse
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.item.model.Item

@Mapper(componentModel = "spring")
abstract class ItemMapper {

    @Mapping(target = "organId", source = "organ.id")
    abstract fun toModel(entity: ItemJpaEntity): Item

    abstract fun toResponse(model: Item): ItemResponse

    abstract fun toQueryResponse(model: Item): ItemQueryResponse

    fun toEntity(model: Item, organ: OrganJpaEntity): ItemJpaEntity {
        return ItemJpaEntity(
            itemId = null,
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
