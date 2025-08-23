package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.team.entity.StockJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.Stock

@Mapper(componentModel = "spring")
abstract class StockMapper {

    @Mapping(target = "teamId", source = "team.id")
    abstract fun toModel(entity: StockJpaEntity): Stock

    fun toEntity(model: Stock, team: TeamJpaEntity): StockJpaEntity {
        return StockJpaEntity(
            team = team,
            itemName = model.itemName,
            itemCount = model.itemCount,
            buyMoney = model.buyMoney,
            valProfit = model.valProfit,
            profitNum = model.profitNum
        )
    }
}
