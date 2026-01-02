package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.mapper.ArticleMapper
import team.mozu.dsm.application.port.`in`.article.QueryArticlesUseCase
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.application.port.out.auth.SecurityPort

@Service
class QueryArticlesService(
    private val queryArticlePort: QueryArticlePort,
    private val articleMapper: ArticleMapper,
    private val securityPort: SecurityPort
) : QueryArticlesUseCase {

    @Transactional(readOnly = true)
    override fun execute(): List<ArticleResponse> =
        queryArticlePort.findAllByOrganId(securityPort.getCurrentOrgan().id!!)
            .map { articleMapper.toResponse(it) }
}
