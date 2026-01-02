package team.mozu.dsm.adapter.out.article.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.article.entity.QArticleJpaEntity.articleJpaEntity
import team.mozu.dsm.adapter.out.article.mapper.ArticleMapper
import team.mozu.dsm.adapter.out.article.persistence.repository.ArticleJpaRepository
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganJpaRepository
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.out.article.ArticlePort
import team.mozu.dsm.domain.article.model.Article
import java.util.UUID

@Component
class ArticlePersistenceAdapter(
    private val articleRepository: ArticleJpaRepository,
    private val articleMapper: ArticleMapper,
    private val organRepository: OrganJpaRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : ArticlePort {

    //--Query--//
    override fun existsById(id: UUID): Boolean =
        articleRepository.existsById(id)

    override fun findAllByIds(ids: Set<UUID>): List<Article> =
        articleRepository
            .findAllByIdInAndIsDeletedFalse(ids.toList())
            .map { articleMapper.toModel(it) }

    override fun findById(id: UUID): Article? =
        articleRepository
            .findByIdAndIsDeletedFalse(id)
            ?.let { articleMapper.toModel(it) }

    override fun findAll(): List<Article> =
        articleRepository.findAll()
            .map { articleMapper.toModel(it) }

    override fun findAllByOrganId(organId: UUID): List<Article> =
        articleRepository
            .findAllByOrgan_IdAndIsDeletedFalse(organId)
            .map { articleMapper.toModel(it) }

    //--Command--//
    override fun save(article: Article): Article {
        val organ = organRepository.findByIdOrNull(article.organId)
            ?: throw OrganNotFoundException
        val saveArticle = articleRepository.save(articleMapper.toEntity(article, organ))

        return articleMapper.toModel(saveArticle)
    }

    override fun delete(article: Article) {
        jpaQueryFactory
            .update(articleJpaEntity)
            .set(articleJpaEntity.isDeleted, true)
            .where(articleJpaEntity.id.eq(article.id))
            .execute()
    }
}
