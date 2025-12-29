package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.mapper.ArticleMapper
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.port.`in`.article.QueryArticleDetailUseCase
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import java.util.UUID

@Service
class QueryArticleDetailService(
    private val queryArticlePort: QueryArticlePort,
    private val articleMapper: ArticleMapper
) : QueryArticleDetailUseCase {

    @Transactional(readOnly = true)
    override fun execute(id: UUID): ArticleResponse =
        articleMapper.toResponse(
            queryArticlePort.findById(id)
                ?: throw ArticleNotFoundException
        )
}
