package team.mozu.dsm.application.port.`in`.article

import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse

interface QueryArticlesUseCase {

    fun execute(): List<ArticleResponse>
}
