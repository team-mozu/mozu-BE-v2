package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundArticleResponse
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.lesson.GetLessonRoundArticlesUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonArticlePort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort

@Service
class GetLessonRoundArticlesService(
    private val lessonPort: QueryLessonPort,
    private val lessonArticlePort: QueryLessonArticlePort
) : GetLessonRoundArticlesUseCase {

    @Transactional(readOnly = true)
    override fun get(lessonNum: String): List<LessonRoundArticleResponse> {
        val lesson = lessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException
        val nowInvRound = lesson.curInvRound

        return lessonArticlePort.findAllRoundArticlesByLessonId(lesson.id!!, nowInvRound)
    }
}
