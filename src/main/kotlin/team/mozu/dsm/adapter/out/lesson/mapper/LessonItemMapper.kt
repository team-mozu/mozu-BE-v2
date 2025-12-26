package team.mozu.dsm.adapter.out.lesson.mapper

import org.mapstruct.Mapper
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonItemId
import team.mozu.dsm.domain.lesson.model.LessonItem

@Mapper(componentModel = "spring")
abstract class LessonItemMapper {

    abstract fun toModel(entity: LessonItemJpaEntity): LessonItem

    fun toEntity(model: LessonItem, lesson: LessonJpaEntity, item: ItemJpaEntity): LessonItemJpaEntity {
        return LessonItemJpaEntity(
            lessonItemId = LessonItemId(
                model.lessonItemId.lessonId,
                model.lessonItemId.itemId
            ),
            lesson = lesson,
            item = item,
            round1Price = model.round1Price,
            round2Price = model.round2Price,
            round3Price = model.round3Price,
            round4Price = model.round4Price,
            round5Price = model.round5Price,
            endPrice = model.endPrice
        )
    }
}
