package team.mozu.dsm.adapter.out.item.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.`class`.entity.ClassJpaEntity
import java.util.UUID

interface ItemRepository : JpaRepository<ClassJpaEntity, UUID>
