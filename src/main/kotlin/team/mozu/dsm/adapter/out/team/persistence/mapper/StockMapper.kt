package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.team.entity.StockJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.Stock

@Mapper(componentModel = "spring")
interface StockMapper {

    /**
     * 상속 필드는 Kotlin 생성자 매핑 시 자동 연결되지 않을 수 있으므로
     * 명시적으로 @Mapping을 지정함
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "teamId", source = "team.id")
    fun toModel(entity: StockJpaEntity): Stock

    /**
     * MapStruct는 DB 조회를 지원하지 않으므로 toEntity 메서드는 수동 구현 방식을 사용함
     * (StockJpaEntity에는 TeamJpaEntity 객체 필드만 존재 -> teamId를 엔티티에서 찾으려면 DB 조회가 필요)
     */
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
