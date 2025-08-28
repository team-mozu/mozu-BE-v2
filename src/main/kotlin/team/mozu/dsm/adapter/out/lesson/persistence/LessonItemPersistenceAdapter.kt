package team.mozu.dsm.adapter.out.lesson.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonItemMapper
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonItemRepository
import team.mozu.dsm.application.port.out.lesson.LessonItemQueryPort
import team.mozu.dsm.domain.lesson.model.LessonItem
import java.util.UUID

@Component
class LessonItemPersistenceAdapter(
    private val lessonItemRepository: LessonItemRepository,
    private val lessonItemMapper: LessonItemMapper
) : LessonItemQueryPort {

    override fun findItemIdsByLessonId(lessonId: UUID): List<UUID> {
        return lessonItemRepository.findItemIdsByLessonId(lessonId)
    }

    override fun findAllByLessonIdAndItemIds(lessonId: UUID, itemIds: List<UUID>): List<LessonItem> {
        if (itemIds.isEmpty()) return emptyList()

        val entities = lessonItemRepository.findAllByLessonIdAndItemIdIn(lessonId, itemIds)
        return entities.map { lessonItemMapper.toModel(it) }
    }
}
