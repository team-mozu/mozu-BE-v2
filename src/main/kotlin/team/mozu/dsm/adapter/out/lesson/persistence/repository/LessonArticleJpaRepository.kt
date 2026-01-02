package team.mozu.dsm.adapter.out.lesson.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.lesson.entity.LessonArticleJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonArticleId
import java.util.UUID

interface LessonArticleJpaRepository : JpaRepository<LessonArticleJpaEntity, LessonArticleId> {

    fun findAllByLessonId(lessonId: UUID): List<LessonArticleJpaEntity>

    fun findAllByLesson_IdAndArticle_IsDeletedFalse(lessonId: UUID): List<LessonArticleJpaEntity>
}
