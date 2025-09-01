package team.mozu.dsm.application.port.out.article

import team.mozu.dsm.domain.article.model.Article
import java.util.UUID

interface QueryArticlePort {

    fun existsById(id: UUID): Boolean

    fun findAllByIds(ids: Set<UUID>): List<Article>
}
