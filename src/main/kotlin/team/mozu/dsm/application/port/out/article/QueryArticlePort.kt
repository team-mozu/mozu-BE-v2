package team.mozu.dsm.application.port.out.article

import team.mozu.dsm.domain.article.model.Article
import team.mozu.dsm.domain.item.model.Item
import java.util.UUID

interface QueryArticlePort {

    fun existsById(id: UUID): Boolean

    fun findAllByIds(ids: Set<UUID>): List<Article>

    fun findById(id: UUID): Article?

    fun findAll(): List<Article>
}
