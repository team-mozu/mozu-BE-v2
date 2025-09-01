package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import team.mozu.dsm.application.port.`in`.lesson.GetLessonDetailUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonArticlePort
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.*

@Service
class GetLessonDetailService(
    private val lessonFacade: LessonFacade,
    private val lessonItemPort: QueryLessonItemPort,
    private val lessonArticlePort: QueryLessonArticlePort
): GetLessonDetailUseCase {

    override fun get(id: UUID): LessonResponse {
        val lesson = lessonFacade.findByLessonId(id)

        val lessonItems = lessonItemPort.findAllByLessonId(lesson.id!!)
        val lessonArticles = lessonArticlePort.findAllByLessonId(lesson.id)

        val lessonItemResponses = lessonFacade.toLessonItemResponses(lessonItems)
        val lessonArticleResponses = lessonFacade.toLessonArticleResponses(lessonArticles)

        return LessonResponse(
            id = lesson.id,
            name = lesson.lessonName,
            maxInvRound = lesson.maxInvRound,
            curInvRound = lesson.curInvRound,
            baseMoney = lesson.baseMoney,
            lessonNum = lesson.lessonNum,
            isInProgress = lesson.isInProgress,
            isStarred = lesson.isStarred,
            isDeleted = lesson.isDeleted,
            createdAt = lesson.createdAt!!,
            lessonItems = lessonItemResponses,
            lessonArticles = lessonArticleResponses
        )
    }
}
