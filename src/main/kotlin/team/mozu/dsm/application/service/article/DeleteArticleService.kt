package team.mozu.dsm.application.service.article

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.exception.lesson.CannotDeleteLessonException
import team.mozu.dsm.application.port.`in`.article.DeleteArticleUseCase
import team.mozu.dsm.application.port.out.article.CommandArticlePort
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.application.port.out.auth.SecurityPort
import java.util.*

@Service
class DeleteArticleService(
    private val commandArticlePort: CommandArticlePort,
    private val queryArticlePort: QueryArticlePort,
    private val securityPort: SecurityPort
) : DeleteArticleUseCase {

    @Transactional
    override fun delete(id: UUID) {
        val organ = securityPort.getCurrentOrgan()
        val article = queryArticlePort.findById(id)
            ?: throw ArticleNotFoundException

        if (article.organId != organ.id) {
            throw CannotDeleteLessonException
        }

        commandArticlePort.delete(article)
    }
}
