package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.exception.lesson.CannotDeleteLessonException
import team.mozu.dsm.application.port.`in`.article.UpdateArticleUseCase
import team.mozu.dsm.application.port.out.article.CommandArticlePort
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.s3.S3Port
import java.time.LocalDateTime
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
    override fun update(id: UUID, request: ArticleRequest): ArticleResponse {
        val organ = securityPort.getCurrentOrgan()
        val article = queryArticlePort.findById(id) ?: throw ArticleNotFoundException

        if (article.organId != organ.id) {
            throw CannotDeleteLessonException
        }

        val newImageUrl: String? = request.articleImage
            ?.takeIf { !it.isEmpty }
            ?.let { s3Port.upload(it) }

        val updated = article.copy(
            articleName = request.articleName,
            articleDesc = request.articleDesc,
            articleImage = newImageUrl ?: article.articleImage,
            updatedAt = LocalDateTime.now()
        )

        val saved = commandArticlePort.save(updated)
        return articleMapper.toResponse(saved)
    }
}
