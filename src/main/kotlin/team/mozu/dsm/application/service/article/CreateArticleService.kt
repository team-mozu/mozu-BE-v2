package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.port.`in`.article.CreateArticleUseCase
import team.mozu.dsm.application.port.out.article.CommandArticlePort
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.domain.article.model.Article
import java.time.LocalDateTime
import java.util.*

@Service
class CreateArticleService(
    private val securityPort: SecurityPort,
    private val articlePort: CommandArticlePort,
    private val articleMapper: ArticleMapper
) : CreateArticleUseCase {

    @Transactional
    override fun create(request: ArticleRequest): ArticleResponse {
        val organ = securityPort.getCurrentOrgan()

        val article = Article(
            id = UUID.randomUUID(),
            organId = organ.id!!,
            articleName = request.articleName,
            articleImage = null,
            articleDesc = request.articleDesc,
            isDeleted = false,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val saved = articlePort.save(article)

        return articleMapper.toResponse(saved)
    }
}
