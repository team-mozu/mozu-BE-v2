package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.article.dto.request.UpdateArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.exception.article.CannotDeleteArticleException
import team.mozu.dsm.application.exception.lesson.CannotDeleteLessonException
import team.mozu.dsm.application.port.`in`.article.UpdateArticleUseCase
import team.mozu.dsm.application.port.out.article.CommandArticlePort
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.s3.S3Port
import team.mozu.dsm.domain.article.model.Article
import java.util.*

@Service
class UpdateArticleService(
    private val commandArticlePort: CommandArticlePort,
    private val queryArticlePort: QueryArticlePort,
    private val articleMapper: ArticleMapper,
    private val securityPort: SecurityPort,
    private val s3Port: S3Port
) : UpdateArticleUseCase {

    @Transactional
    override fun update(id: UUID, request: UpdateArticleRequest): ArticleResponse {
        val organ = securityPort.getCurrentOrgan()
        val article: Article = queryArticlePort.findById(id) ?: throw ArticleNotFoundException

        if (article.organId != organ.id) {
            throw CannotDeleteArticleException
        }

        val newImageUrl: String? = when {
            request.articleImage != null && !request.articleImage.isEmpty -> {
                s3Port.upload(request.articleImage)
            }
            !request.articleImageUrl.isNullOrBlank() -> {
                request.articleImageUrl
            }
            else -> article.articleImage
        }

        val saveArticle = commandArticlePort.save(article.updateArticle(request, newImageUrl))
        return articleMapper.toResponse(saveArticle)
    }
}
