package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleQueryResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.port.`in`.article.QueryArticleAllUseCase
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.application.port.out.auth.SecurityPort

@Service
class QueryArticleAllService(
    private val queryArticlePort: QueryArticlePort,
    private val articleMapper: ArticleMapper,
    private val securityPort: SecurityPort
) : QueryArticleAllUseCase {

    @Transactional(readOnly = true)
    override fun queryAll(): List<ArticleQueryResponse> {
        val currentOrgan = securityPort.getCurrentOrgan()

        return queryArticlePort.findAllByOrganId(currentOrgan.id!!).map { articleMapper.toQueryResponse(it) }
    }
}
