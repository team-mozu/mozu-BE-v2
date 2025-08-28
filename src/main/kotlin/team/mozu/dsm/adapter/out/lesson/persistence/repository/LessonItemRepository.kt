package team.mozu.dsm.adapter.out.lesson.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonItemId
import java.util.UUID

interface LessonItemRepository : JpaRepository<LessonItemJpaEntity, LessonItemId>
