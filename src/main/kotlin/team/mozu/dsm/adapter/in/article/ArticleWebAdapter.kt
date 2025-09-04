package team.mozu.dsm.adapter.`in`.article

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.application.port.`in`.article.CreateArticleUseCase

@RestController
@RequestMapping("/article")
class ArticleWebAdapter(
    private val createArticleUseCase: CreateArticleUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid
        request: ArticleRequest
    ): ArticleResponse {
        return createArticleUseCase.create(request)
    }
}
