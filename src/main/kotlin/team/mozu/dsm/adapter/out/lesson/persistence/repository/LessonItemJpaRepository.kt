package team.mozu.dsm.adapter.out.lesson.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonItemId
import java.util.UUID

interface LessonItemJpaRepository : JpaRepository<LessonItemJpaEntity, LessonItemId> {

    fun findAllByLesson_IdAndItem_IsDeletedFalse(lessonId: UUID): List<LessonItemJpaEntity>
}
