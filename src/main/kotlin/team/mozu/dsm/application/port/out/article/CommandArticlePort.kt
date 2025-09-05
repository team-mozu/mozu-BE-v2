package team.mozu.dsm.application.port.out.article

import team.mozu.dsm.domain.article.model.Article

interface CommandArticlePort {
    fun save(article: Article): Article

    fun delete(article: Article)
}
