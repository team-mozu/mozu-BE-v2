package team.mozu.dsm.adapter.out.lesson.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import java.util.UUID

interface LessonRepository : JpaRepository<LessonJpaEntity, UUID>
