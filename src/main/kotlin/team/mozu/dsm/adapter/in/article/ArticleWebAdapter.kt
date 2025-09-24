package team.mozu.dsm.adapter.`in`.article

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleQueryResponse
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.port.`in`.article.*
import team.mozu.dsm.global.document.article.ArticleApiDocument
import java.util.*

@RestController
@RequestMapping("/article")
class ArticleWebAdapter(
    private val createArticleUseCase: CreateArticleUseCase,
    private val queryArticleDetailUseCase: QueryArticleDetailUseCase,
    private val queryArticleAllUseCase: QueryArticleAllUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    private val updateArticleUseCase: UpdateArticleUseCase,
    private val articleMapper: ArticleMapper
) : ArticleApiDocument {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(
        @ModelAttribute @Valid
        request: ArticleRequest
    ): ArticleResponse {
        return createArticleUseCase.create(request)
    }

    @GetMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.OK)
    override fun queryDetail(
        @PathVariable id: UUID
    ): ArticleResponse {
        val article = queryArticleDetailUseCase.queryDetail(id)
        return articleMapper.toResponse(article)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun queryAll(): List<ArticleQueryResponse> {
        return queryArticleAllUseCase.queryAll()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun delete(
        @PathVariable id: UUID
    ) {
        deleteArticleUseCase.delete(id)
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid
        request: ArticleRequest
    ): ArticleResponse {
        return updateArticleUseCase.update(id, request)
    }
}
