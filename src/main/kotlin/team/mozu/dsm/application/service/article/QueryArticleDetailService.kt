package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.port.`in`.article.QueryArticleDetailUseCase
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.domain.article.model.Article
import java.util.*

@Service
class QueryArticleDetailService (
    private val queryArticlePort: QueryArticlePort
) : QueryArticleDetailUseCase{

    @Transactional(readOnly = true)
    override fun queryDetail(id: UUID): Article {
        return queryArticlePort.findById(id)
            ?: throw ItemNotFoundException
    }
}
