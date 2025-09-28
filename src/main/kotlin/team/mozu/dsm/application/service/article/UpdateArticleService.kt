package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.article.dto.request.UpdateArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.exception.article.CannotDeleteArticleException
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
            // 1. 이미지 삭제: articleImageUrl = "" (빈 문자열)
            request.articleImageUrl == "" -> {
                // 기존 이미지가 있다면 S3에서 삭제
                article.articleImage?.let { s3Port.delete(it) }
                null
            }
            // 2. 새 이미지 업로드: File 객체
            request.articleImage != null && !request.articleImage.isEmpty -> {
                // 기존 이미지가 있다면 S3에서 삭제
                article.articleImage?.let { s3Port.delete(it) }
                s3Port.upload(request.articleImage)
            }
            // 3. 변경 없음: null (기존 이미지 유지)
            request.articleImageUrl == null -> article.articleImage
            // 4. URL로 이미지 교체: articleImageUrl에 새 URL 제공 (빈 문자열이 아닌 경우)
            else -> {
                // 기존 이미지가 있다면 S3에서 삭제
                article.articleImage?.let { s3Port.delete(it) }
                request.articleImageUrl
            }
        }

        val saveArticle = commandArticlePort.save(article.updateArticle(request, newImageUrl))
        return articleMapper.toResponse(saveArticle)
    }
}
