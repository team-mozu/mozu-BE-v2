package team.mozu.dsm.adapter.out.lesson.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonItemId
import java.util.*

interface LessonItemRepository : JpaRepository<LessonItemJpaEntity, LessonItemId> {

    fun findAllByLessonId(lessonId: UUID): List<LessonItemJpaEntity>
}
