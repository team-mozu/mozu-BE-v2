package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.port.`in`.article.UpdateArticleUseCase
import team.mozu.dsm.application.port.out.article.CommandArticlePort
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateArticleService (
    private val commandArticlePort: CommandArticlePort,
    private val queryArticlePort: QueryArticlePort,
    private val articleMapper: ArticleMapper
) : UpdateArticleUseCase{

    @Transactional
    override fun update(id: UUID, request: ArticleRequest): ArticleResponse {
        val article = queryArticlePort.findById(id) ?: throw ArticleNotFoundException

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
