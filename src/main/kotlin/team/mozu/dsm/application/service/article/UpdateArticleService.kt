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
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateArticleService(
    private val commandArticlePort: CommandArticlePort,
    private val queryArticlePort: QueryArticlePort,
    private val articleMapper: ArticleMapper,
    private val securityPort: SecurityPort
) : UpdateArticleUseCase {

    @Transactional
    override fun update(id: UUID, request: ArticleRequest): ArticleResponse {
        val organ = securityPort.getCurrentOrgan()
        val article = queryArticlePort.findById(id) ?: throw ArticleNotFoundException

        if (article.organId != organ.id) {
            throw CannotDeleteLessonException
        }

        val updated = article.copy(
            articleName = request.articleName,
            articleDesc = request.articleDesc,
            articleImage = request.articleImage,
            updatedAt = LocalDateTime.now()
        )

        val saved = commandArticlePort.save(updated)
        return articleMapper.toResponse(saved)
    }
}
