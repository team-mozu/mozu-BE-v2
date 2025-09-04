package team.mozu.dsm.application.port.`in`.article

import team.mozu.dsm.domain.article.model.Article
import java.util.UUID

interface QueryArticleDetailUseCase {

    fun queryDetail(id: UUID): Article
}
