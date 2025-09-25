package team.mozu.dsm.application.port.`in`.article

import org.springframework.web.multipart.MultipartFile
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import java.util.*

interface UpdateArticleUseCase {

    fun update(id: UUID, request: ArticleRequest, image: MultipartFile): ArticleResponse
}
