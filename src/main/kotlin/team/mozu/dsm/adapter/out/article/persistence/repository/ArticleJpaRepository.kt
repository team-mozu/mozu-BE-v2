package team.mozu.dsm.adapter.out.article.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.article.entity.ArticleJpaEntity
import java.util.UUID

interface ArticleJpaRepository : JpaRepository<ArticleJpaEntity, UUID> {

    fun findByIdAndIsDeletedFalse(id: UUID): ArticleJpaEntity?

    fun findAllByIdInAndIsDeletedFalse(ids: List<UUID>): List<ArticleJpaEntity>

    fun findAllByOrganIdAndIsDeletedFalse(organId: UUID): List<ArticleJpaEntity>
}
