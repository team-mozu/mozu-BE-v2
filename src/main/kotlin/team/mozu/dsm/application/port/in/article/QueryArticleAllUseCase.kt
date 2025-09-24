package team.mozu.dsm.application.port.`in`.article

import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleQueryResponse

interface QueryArticleAllUseCase {

    fun queryAll(): List<ArticleQueryResponse>
}
