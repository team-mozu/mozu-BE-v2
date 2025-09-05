package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import team.mozu.dsm.application.port.`in`.lesson.CreateLessonUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import team.mozu.dsm.domain.lesson.model.Lesson

@Service
class CreateLessonService(
    private val securityPort: SecurityPort,
    private val lessonPort: CommandLessonPort,
    private val lessonFacade: LessonFacade
) : CreateLessonUseCase {

    @Transactional
    override fun create(request: LessonRequest): LessonResponse {
        // 로그인한 기관 정보 조회
        val organ = securityPort.getCurrentOrgan()

        // 요청된 lessonItems에 해당 종목이 DB에 존재하는지 확인
        lessonFacade.validateItemsExists(request.lessonItems)

        // 요청된 lessonArticles 해당 기사가 DB에 존재하는지 확인
        lessonFacade.validateArticlesExists(request.lessonArticles)

        // Lesson 도메인 객체 생성 후 DB에 저장
        val lesson = Lesson(
            organId = organ.id!!,
            lessonName = request.lessonName,
            maxInvRound = request.lessonRound,
            curInvRound = 0,
            baseMoney = request.baseMoney,
            isStarred = false,
            isDeleted = false,
            isInProgress = false
        )
        val saved = lessonPort.save(lesson)

        // LessonItemRequest를 LessonItem 도메인으로 변환하고 DB에 저장
        val lessonItems = lessonFacade.saveLessonItems(saved, request.lessonRound, request.lessonItems)

        // LessonArticleRequest를 LessonArticle 도메인으로 변환하고 DB에 저장
        val lessonArticles = lessonFacade.saveLessonArticles(saved, request.lessonArticles)

        // LessonItems DTO 변환
        val lessonItemResponses = lessonFacade.toLessonItemResponses(lessonItems)

        // LessonArticles DTO 변환
        val lessonArticleResponses = lessonFacade.toLessonArticleResponses(lessonArticles)

        // 최종 LessonResponse 반환
        return LessonResponse(
            id = saved.id!!,
            name = saved.lessonName,
            maxInvRound = saved.maxInvRound,
            curInvRound = saved.curInvRound,
            baseMoney = saved.baseMoney,
            lessonNum = saved.lessonNum,
            isInProgress = saved.isInProgress,
            isStarred = saved.isStarred,
            isDeleted = saved.isDeleted,
            createdAt = saved.createdAt!!,
            lessonItems = lessonItemResponses,
            lessonArticles = lessonArticleResponses
        )
    }
}
