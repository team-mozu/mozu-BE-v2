package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.port.`in`.article.QueryArticleAllUseCase
import team.mozu.dsm.application.port.out.article.QueryArticlePort

@Service
class QueryArticleAllService (
    private val queryArticlePort: QueryArticlePort,
    private val articleMapper: ArticleMapper
) : QueryArticleAllUseCase{

    @Transactional(readOnly = true)
    override fun queryAll(): List<ArticleResponse> {
        return queryArticlePort.findAll().map { articleMapper.toResponse(it) }
    }
}
