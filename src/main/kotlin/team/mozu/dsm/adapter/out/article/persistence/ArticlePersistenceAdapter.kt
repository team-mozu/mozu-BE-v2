package team.mozu.dsm.adapter.out.article.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.adapter.out.article.persistence.repository.ArticleRepository
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.domain.article.model.Article
import java.util.*

@Component
class ArticlePersistenceAdapter(
    private val articleRepository: ArticleRepository,
    private val articleMapper: ArticleMapper
) : QueryArticlePort {

    //--Query--//
    override fun existsById(id: UUID): Boolean {
        return articleRepository.existsById(id)
    }

    override fun findAllByIds(ids: Set<UUID>): List<Article> {
        return articleRepository.findAllById(ids)
            .map { articleMapper.toModel(it) }
    }

    //--Command--//
}
