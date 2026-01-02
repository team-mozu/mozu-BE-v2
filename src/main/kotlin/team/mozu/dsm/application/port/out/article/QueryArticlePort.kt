package team.mozu.dsm.application.port.out.article

import team.mozu.dsm.domain.article.model.Article
import java.util.UUID

interface QueryArticlePort {

    fun findAllByIds(ids: Set<UUID>): List<Article>

    fun findById(id: UUID): Article?

    fun findAllByOrganId(organId: UUID): List<Article>
}
