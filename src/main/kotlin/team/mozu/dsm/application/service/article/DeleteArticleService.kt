package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.port.`in`.article.DeleteArticleUseCase
import team.mozu.dsm.application.port.out.article.CommandArticlePort
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import java.util.*

@Service
class DeleteArticleService (
    private val commandArticlePort: CommandArticlePort,
    private val queryArticlePort: QueryArticlePort
) : DeleteArticleUseCase{


    @Transactional
    override fun delete(id: UUID) {
        val article = queryArticlePort.findById(id)
            ?: throw ArticleNotFoundException

        commandArticlePort.delete(article)
    }
}
