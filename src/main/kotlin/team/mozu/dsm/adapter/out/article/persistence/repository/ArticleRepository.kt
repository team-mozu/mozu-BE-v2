package team.mozu.dsm.adapter.out.article.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.article.entity.ArticleJpaEntity
import java.util.UUID

interface ArticleRepository : JpaRepository<ArticleJpaEntity, UUID>
