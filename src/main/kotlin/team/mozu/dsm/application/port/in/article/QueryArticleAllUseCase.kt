package team.mozu.dsm.application.port.`in`.article

import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse

interface QueryArticleAllUseCase {

    fun queryAll(): List<ArticleResponse>
}
