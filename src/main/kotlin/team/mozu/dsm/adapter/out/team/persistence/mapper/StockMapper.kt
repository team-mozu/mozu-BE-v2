package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonItemMapper
import team.mozu.dsm.adapter.out.team.entity.StockJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.Stock

@Mapper(
    componentModel = "spring",
    uses = [LessonItemMapper::class]
)
abstract class StockMapper {

    /**
     * 연관관계 엔티티(team)는 직접 매핑되지 않으므로
     * PK(team.id)를 target(teamId)과 매핑하기 위해 명시적으로 지정
     */
    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "lessonItemId", source = "lessonItem")
    abstract fun toModel(entity: StockJpaEntity): Stock

    /**
     * MapStruct는 DB 조회를 지원하지 않으므로 toEntity 메서드는 수동 구현 방식을 사용함
     * (StockJpaEntity에는 TeamJpaEntity 객체 필드만 존재 -> teamId를 엔티티에서 찾으려면 DB 조회가 필요)
     */
    fun toEntity(model: Stock, team: TeamJpaEntity, lessonItem: LessonItemJpaEntity): StockJpaEntity {
        return StockJpaEntity(
            team = team,
            lessonItem = lessonItem,
            itemName = model.itemName,
            avgPurchasePrice = model.avgPurchasePrice,
            quantity = model.quantity,
            buyMoney = model.buyMoney,
            valProfit = model.valProfit,
            profitNum = model.profitNum
        )
    }
}
