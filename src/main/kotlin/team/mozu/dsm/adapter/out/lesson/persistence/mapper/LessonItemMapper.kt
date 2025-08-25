package team.mozu.dsm.adapter.out.lesson.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonItemId
import team.mozu.dsm.domain.lesson.model.LessonItem

@Mapper(componentModel = "spring")
abstract class LessonItemMapper {

    @Mapping(target = "lessonItemId", source = "lessonItemId")
    abstract fun toModel(entity: LessonItemJpaEntity): LessonItem

    fun toEntity(model: LessonItem, lesson: LessonJpaEntity, item: ItemJpaEntity): LessonItemJpaEntity {
        return LessonItemJpaEntity(
            lessonItemId = LessonItemId(
                model.lessonItemId.lessonId,
                model.lessonItemId.itemId
            ),
            lesson = lesson,
            item = item,
            currentMoney = model.currentMoney,
            round1Money = model.round1Money,
            round2Money = model.round2Money,
            round3Money = model.round3Money,
            round4Money = model.round4Money,
            round5Money = model.round5Money,
            round6Money = model.round6Money,
            round7Money = model.round7Money,
            round8Money = model.round8Money
        )
    }
}
