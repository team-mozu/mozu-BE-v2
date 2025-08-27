package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.ArticleResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonArticleResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.port.`in`.lesson.CreateLessonUseCase
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonArticlePort
import team.mozu.dsm.application.port.out.lesson.CommandLessonItemPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.domain.lesson.model.Lesson
import team.mozu.dsm.domain.lesson.model.LessonArticle
import team.mozu.dsm.domain.lesson.model.LessonItem
import team.mozu.dsm.domain.lesson.model.id.LessonArticleId
import team.mozu.dsm.domain.lesson.model.id.LessonItemId

@Service
class CreateLessonService(
    private val securityPort: SecurityPort,
    private val itemPort: QueryItemPort,
    private val articlePort: QueryArticlePort,
    private val lessonPort: CommandLessonPort,
    private val lessonItemPort: CommandLessonItemPort,
    private val lessonArticlePort: CommandLessonArticlePort
) : CreateLessonUseCase {

    @Transactional
    override fun create(request: LessonRequest): LessonResponse {
        // 로그인한 기관 정보 조회
        val organ = securityPort.getCurrentOrgan()

        // 요청된 lessonItems에 해당 종목이 DB에 존재하는지 확인
        if (request.lessonItems.any { !itemPort.existsById(it.id) }) {
            throw ItemNotFoundException
        }

        // 요청된 lessonArticles에 해당 기사가 DB에 존재하는지 확인
        if (request.lessonArticles.any { lessonArticle ->
            lessonArticle.articles.any { !articlePort.existsById(it) }
        }
        ) {
            throw ArticleNotFoundException
        }

        // Lesson 도메인 객체 생성 후 DB에 저장
        // 해당 Lesson 객체를 생성할 때 createdAt은 JPA가 관리해주기 때문에
        // nullable로 하고 생성시 넣지 않았는데, not null로 해야할 것 같으면 말씀해주세요!!
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

        // lessonItems로 LessonItem 도메인으로 변환 후 saveAll로 한 번에 저장
        val lessonItems = request.lessonItems.map { req ->
            LessonItem(
                lessonItemId = LessonItemId(saved.id!!, req.id),
                currentMoney = req.money.get(0),
                round1Money = req.money.get(1),
                round2Money = req.money.get(2),
                round3Money = req.money.get(3),
                round4Money = req.money.getOrNull(4),
                round5Money = req.money.getOrNull(5)
            )
        }
        lessonItemPort.saveAll(saved.id!!, lessonItems)

        // lessonArticles로 LessonArticle 도메인으로 변환 후 saveAll로 한 번에 저장
        val lessonArticles = request.lessonArticles.flatMap { req ->
            req.articles.map { article ->
                LessonArticle(
                    lessonArticleId = LessonArticleId(saved.id!!, article),
                    investmentRound = req.investmentRound
                )
            }
        }
        lessonArticlePort.saveAll(saved.id!!, lessonArticles)

        // 저장된 기사 정보 조회 후 Map 생성 (ID → Article)
        val articleIds = lessonArticles.map { it.lessonArticleId.articleId }
        val articleMap = articlePort.findAllByIds(articleIds).associateBy { it.id!! }

        // DTO 변환: 투자 차수별로 기사 묶음 생성
        val lessonArticleResponses = lessonArticles
            .groupBy { it.investmentRound }
            .map { (round, articles) ->
                LessonArticleResponse(
                    investmentRound = round,
                    articles = articles.map { lessonArticle ->
                        val article = articleMap[lessonArticle.lessonArticleId.articleId]
                            ?: throw ArticleNotFoundException
                        ArticleResponse(
                            articleId = article.id!!,
                            title = article.articleName
                        )
                    }
                )
            }

        // 저장된 종목 정보 조회 후 Map 생성 (ID → Item)
        val itemIds = lessonItems.map { it.lessonItemId.itemId }
        val itemMap = itemPort.findAllByIds(itemIds).associateBy { it.id!! }

        // DTO 변환: 금액 리스트 구성 (초기 금액 ~ 3차 + nullable 4 ~ 5차)
        val lessonItemResponses = lessonItems.map { lessonItem ->
            val item = itemMap[lessonItem.lessonItemId.itemId]
                ?: throw ItemNotFoundException
            LessonItemResponse(
                itemId = item.id!!,
                itemName = item.itemName,
                money = listOf(
                    lessonItem.currentMoney,
                    lessonItem.round1Money,
                    lessonItem.round2Money,
                    lessonItem.round3Money
                ).let { list ->
                    listOfNotNull(*list.toTypedArray(), lessonItem.round4Money, lessonItem.round5Money)
                }
            )
        }

        // 최종 LessonResponse 반환
        return LessonResponse(
            id = saved.id,
            name = saved.lessonName,
            maxInvRound = saved.maxInvRound,
            curInvRound = saved.curInvRound,
            baseMoney = saved.baseMoney,
            lessonNum = saved.lessonNum,
            isStarred = saved.isStarred,
            isDeleted = saved.isDeleted,
            createdAt = saved.createdAt!!,
            lessonArticles = lessonArticleResponses,
            lessonItems = lessonItemResponses
        )
    }
}
