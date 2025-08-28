package team.mozu.dsm.adapter.out.lesson.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonItemId
import java.util.UUID

interface LessonItemRepository : JpaRepository<LessonItemJpaEntity, LessonItemId> {

    @Query("SELECT li.lessonItemId.itemId FROM LessonItemJpaEntity li WHERE li.lessonItemId.lessonId = :lessonId")
    fun findItemIdsByLessonId(@Param("lessonId") lessonId: UUID): List<UUID>

    fun findAllByLessonIdAndItemIdIn(lessonId: UUID, itemIds: List<UUID>): List<LessonItemJpaEntity>
}
