package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.port.`in`.article.CreateArticleUseCase
import team.mozu.dsm.application.port.out.article.CommandArticlePort
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.domain.article.model.Article
import team.mozu.dsm.application.port.out.s3.S3Port
import java.time.LocalDateTime
import java.util.*

@Service
class CreateArticleService(
    private val securityPort: SecurityPort,
    private val articlePort: CommandArticlePort,
    private val articleMapper: ArticleMapper,
    private val s3Port: S3Port
) : CreateArticleUseCase {

    @Transactional
    override fun create(request: ArticleRequest, image: MultipartFile): ArticleResponse {
        val organ = securityPort.getCurrentOrgan()

        val imageUrl: String? = image
            ?.takeIf { !it.isEmpty }
            ?.let { s3Port.upload(it) }

        val article = Article(
            id = UUID.randomUUID(),
            organId = organ.id!!,
            articleName = request.articleName,
            articleImage = imageUrl,
            articleDesc = request.articleDesc,
            isDeleted = false,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val saved = articlePort.save(article)

        return articleMapper.toResponse(saved)
    }
}
