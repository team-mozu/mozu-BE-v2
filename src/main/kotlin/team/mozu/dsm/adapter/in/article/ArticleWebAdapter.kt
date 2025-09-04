package team.mozu.dsm.adapter.`in`.article

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.adapter.out.article.persistence.mapper.ArticleMapper
import team.mozu.dsm.application.port.`in`.article.CreateArticleUseCase
import team.mozu.dsm.application.port.`in`.article.DeleteArticleUseCase
import team.mozu.dsm.application.port.`in`.article.QueryArticleAllUseCase
import team.mozu.dsm.application.port.`in`.article.QueryArticleDetailUseCase
import java.util.*

@RestController
@RequestMapping("/article")
class ArticleWebAdapter(
    private val createArticleUseCase: CreateArticleUseCase,
    private val queryArticleDetailUseCase: QueryArticleDetailUseCase,
    private val queryArticleAllUseCase: QueryArticleAllUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    private val articleMapper: ArticleMapper
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid
        request: ArticleRequest
    ): ArticleResponse {
        return createArticleUseCase.create(request)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun queryDetail(
        @PathVariable id: UUID
    ): ArticleResponse {
        val article = queryArticleDetailUseCase.queryDetail(id)
        return articleMapper.toResponse(article)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun queryAll(): List<ArticleResponse> {
        return queryArticleAllUseCase.queryAll()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: UUID
    ){
        deleteArticleUseCase.delete(id)
    }
}
