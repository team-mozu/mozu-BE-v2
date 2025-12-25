package team.mozu.dsm.adapter.`in`.article

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.request.UpdateArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleQueryResponse
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.application.port.`in`.article.CreateArticleUseCase
import team.mozu.dsm.application.port.`in`.article.QueryArticleAllUseCase
import team.mozu.dsm.application.port.`in`.article.QueryArticleDetailUseCase
import team.mozu.dsm.application.port.`in`.article.DeleteArticleUseCase
import team.mozu.dsm.application.port.`in`.article.UpdateArticleUseCase
import team.mozu.dsm.global.document.article.ArticleApiDocument
import java.util.UUID

@RestController
@RequestMapping("/article")
class ArticleWebAdapter(
    private val createArticleUseCase: CreateArticleUseCase,
    private val queryArticleDetailUseCase: QueryArticleDetailUseCase,
    private val queryArticleAllUseCase: QueryArticleAllUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    private val updateArticleUseCase: UpdateArticleUseCase
) : ArticleApiDocument {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(
        @ModelAttribute @Valid
        request: ArticleRequest
    ): ArticleResponse {
        return createArticleUseCase.execute(request)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun queryDetail(
        @PathVariable id: UUID
    ): ArticleResponse {
        return queryArticleDetailUseCase.execute(id)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun queryAll(): List<ArticleQueryResponse> {
        return queryArticleAllUseCase.execute()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun delete(
        @PathVariable id: UUID
    ) {
        deleteArticleUseCase.execute(id)
    }

    @PatchMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.OK)
    override fun update(
        @PathVariable id: UUID,
        @ModelAttribute @Valid
        request: UpdateArticleRequest
    ): ArticleResponse {
        return updateArticleUseCase.execute(id, request)
    }
}
