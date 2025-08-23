package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.team.entity.HoldItemJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.HoldItem

@Mapper(componentModel = "spring")
abstract class HoldItemMapper {

    @Mapping(target = "teamId", source = "team.id")
    abstract fun toModel(entity: HoldItemJpaEntity): HoldItem

    fun toEntity(model: HoldItem, team: TeamJpaEntity): HoldItemJpaEntity {
        return HoldItemJpaEntity(
            team = team,
            itemName = model.itemName,
            itemCount = model.itemCount,
            buyMoney = model.buyMoney,
            valProfit = model.valProfit,
            profitNum = model.profitNum
        )
    }
}
