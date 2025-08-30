package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import team.mozu.dsm.application.exception.lesson.CannotUpdateLessonException
import team.mozu.dsm.application.port.`in`.lesson.UpdateLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.command.UpdateLessonCommand
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class UpdateLessonService(
    private val securityPort: SecurityPort,
    private val lessonFacade: LessonFacade,
    private val lessonPort: CommandLessonPort
) : UpdateLessonUseCase {

    @Transactional
    override fun update(id: UUID, request: LessonRequest): LessonResponse {
        val lesson = lessonFacade.findByLessonId(id)
        val organ = securityPort.getCurrentOrgan()

        if (lesson.organId != organ.id) {
            throw CannotUpdateLessonException
        }

        // 요청된 lessonItems에 해당 종목이 DB에 존재하는지 확인
        lessonFacade.validateItemsExists(request.lessonItems)

        // 요청된 lessonArticles 해당 기사가 DB에 존재하는지 확인
        lessonFacade.validateArticlesExists(request.lessonArticles)

        // 일부 필드만 업데이트하기 위해 LessonRequest → Command 변환 후 DB 업데이트
        val command = UpdateLessonCommand(
            lessonName = request.lessonName,
            baseMoney = request.baseMoney,
            lessonRound = request.lessonRound
        )
        lessonPort.update(lesson.id!!, command)

        // LessonItemRequest를 LessonItem 도메인으로 변환하고 DB에 저장
        val lessonItems = lessonFacade.saveLessonItems(lesson, request.lessonRound, request.lessonItems)

        // LessonArticleRequest를 LessonArticle 도메인으로 변환하고 DB에 저장
        val lessonArticles = lessonFacade.saveLessonArticles(lesson, request.lessonArticles)

        // LessonArticles DTO 변환
        val lessonArticleResponses = lessonFacade.toLessonArticleResponses(lessonArticles)

        // LessonItems DTO 변환
        val lessonItemResponses = lessonFacade.toLessonItemResponses(lessonItems)

        // 최종 LessonResponse 반환
        return LessonResponse(
            id = lesson.id,
            name = command.lessonName,
            maxInvRound = command.lessonRound,
            curInvRound = lesson.curInvRound,
            baseMoney = command.baseMoney,
            lessonNum = lesson.lessonNum,
            isStarred = lesson.isStarred,
            isDeleted = lesson.isDeleted,
            createdAt = lesson.createdAt!!,
            lessonArticles = lessonArticleResponses,
            lessonItems = lessonItemResponses
        )
    }
}
