package team.mozu.dsm.adapter.out.article.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.adapter.out.article.persistence.repository.ArticleRepository
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.out.article.CommandArticlePort
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.domain.article.model.Article
import team.mozu.dsm.domain.item.model.Item
import java.util.*

@Component
class ArticlePersistenceAdapter(
    private val articleRepository: ArticleRepository,
    private val articleMapper: ArticleMapper,
    private val organRepository: OrganRepository
) : QueryArticlePort, CommandArticlePort {

    //--Query--//
    override fun existsById(id: UUID): Boolean {
        return articleRepository.existsById(id)
    }

    override fun findAllByIds(ids: Set<UUID>): List<Article> {
        return articleRepository.findAllById(ids)
            .map { articleMapper.toModel(it) }
    }
    override fun findById(id: UUID): Article? {
        return articleRepository.findByIdOrNull(id)
            ?.let { articleMapper.toModel(it) }
    }

    override fun findAll(): List<Article> {
        return articleRepository.findAll()
            .map { articleMapper.toModel(it) }
    }
    //--Command--//
    override fun save(article: Article): Article {
        val organ = organRepository.findByIdOrNull(article.organId)
            ?: throw OrganNotFoundException

        val entity = articleMapper.toEntity(article, organ)
        val saved = articleRepository.save(entity)

        return articleMapper.toModel(saved)
    }
}
