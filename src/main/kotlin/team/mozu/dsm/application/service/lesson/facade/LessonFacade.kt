package team.mozu.dsm.application.service.lesson.facade

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonArticleRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonItemRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.ArticleResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonArticleResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemResponse
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.lesson.InsufficientLessonItemMoneyException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.out.article.QueryArticlePort
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonArticlePort
import team.mozu.dsm.application.port.out.lesson.CommandLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.domain.lesson.model.Lesson
import team.mozu.dsm.domain.lesson.model.LessonArticle
import team.mozu.dsm.domain.lesson.model.LessonItem
import team.mozu.dsm.domain.lesson.model.id.LessonArticleId
import team.mozu.dsm.domain.lesson.model.id.LessonItemId
import java.util.UUID

@Component
class LessonFacade(
    private val queryLessonPort: QueryLessonPort,
    private val itemPort: QueryItemPort,
    private val articlePort: QueryArticlePort,
    private val lessonItemPort: CommandLessonItemPort,
    private val lessonArticlePort: CommandLessonArticlePort
) {

    fun findByLessonId(lessonId: UUID): Lesson {
        return queryLessonPort.findById(lessonId)
            ?: throw LessonNotFoundException
    }

    fun validateItemsExists(lessonItems: List<LessonItemRequest>) {
        // 요청된 LessonItemRequest에서 itemId만 추출하고, 중복이 있을 경우 제거
        val itemIds = lessonItems.map { it.itemId }.toSet()
        // DB에서 itemId에 해당하는 Item 엔티티들을 조회하고, ID만 뽑아 유일한 UUID 집합(Set)으로 변환
        val foundItemIds = itemPort.findAllByIds(itemIds).map { it.id!! }.toSet()

        // 요청된 itemIds의 개수와 DB에서 조회한 itemIds의 개수를 비교하여 같지 않으면 예외 발생
        if (foundItemIds.size != itemIds.size) {
            throw ItemNotFoundException
        }
    }

    fun validateArticlesExists(lessonArticles: List<LessonArticleRequest>) {
        // 요청된 LessonArticleRequest에서 articleId만 추출하고, 중복이 있을 경우 제거
        val articleIds = lessonArticles.flatMap { it.articles }.toSet()
        // DB에서 articleId에 해당하는 Article 엔티티들을 조회하고, ID만 뽑아 유일한 UUID 집합(Set)으로 변환
        val foundArticleIds = articlePort.findAllByIds(articleIds).map { it.id!! }.toSet()

        // 요청된 articleIds 개수와 DB에서 조회한 articleIds 개수를 비교하여 같지 않으면 예외 발생
        if (foundArticleIds.size != articleIds.size) {
            throw ArticleNotFoundException
        }
    }

    fun saveLessonItems(lesson: Lesson, lessonRound: Int, lessonItems: List<LessonItemRequest>): List<LessonItem> {
        // 각 LessonItemRequest의 money 리스트 길이를 검증
        // 각 투자 차수 가격 + 종료가가 모두 존재하지 않으면 예외 발생
        lessonItems.forEach { req ->
            if (req.money.size != lessonRound + 1) {
                throw InsufficientLessonItemMoneyException
            }
        }

        // List<LessonItemRequest> -> LessonItem 도메인으로 변환 후 saveAll로 한 번에 저장
        val lessonItemsToSave = lessonItems.map { req ->
            LessonItem(
                lessonItemId = LessonItemId(lesson.id!!, req.itemId),
                round1Price = req.money.get(0), // 1차가격
                round2Price = req.money.get(1), // 2차가격
                round3Price = req.money.get(2), // 3차가격
                round4Price = req.money.getOrNull(3), // 4차가격 (optional)
                round5Price = req.money.getOrNull(4), // 5차가격 (optional)
                endPrice = req.money.getOrNull(lessonRound) ?: req.money.last() // 종료가 (fallback)
            )
        }
        lessonItemPort.saveAll(lesson.id!!, lessonItemsToSave)

        return lessonItemsToSave
    }

    fun saveLessonArticles(lesson: Lesson, lessonArticles: List<LessonArticleRequest>): List<LessonArticle> {
        // List<LessonArticleRequest> -> LessonArticle 도메인으로 변환 후 saveAll로 한 번에 저장
        val lessonArticlesToSave = lessonArticles.flatMap { req ->
            req.articles.map { article ->
                LessonArticle(
                    lessonArticleId = LessonArticleId(lesson.id!!, article),
                    investmentRound = req.investmentRound
                )
            }
        }
        lessonArticlePort.saveAll(lesson.id!!, lessonArticlesToSave)

        return lessonArticlesToSave
    }

    fun toLessonItemResponses(lessonItems: List<LessonItem>): List<LessonItemResponse> {
        // lessonItems에서 itemId만 추출하고 중복 제거
        val itemIds = lessonItems.map { it.lessonItemId.itemId }.toSet()
        // DB에서 해당 itemId에 맞는 Item 엔티티를 조회 후, ID를 key로 매핑 (삭제되지 않은 것만)
        val itemMap = itemPort.findAllByIds(itemIds).associateBy { it.id!! }

        // LessonItem 도메인을 LessonItemResponse DTO로 변환
        // 존재하지 않거나 삭제된 Item은 제외
        return lessonItems.mapNotNull { lessonItem ->
            val item = itemMap[lessonItem.lessonItemId.itemId] ?: return@mapNotNull null
            LessonItemResponse.of(item, lessonItem)
        }
    }

    fun toLessonArticleResponses(lessonArticles: List<LessonArticle>): List<LessonArticleResponse> {
        // lessonArticles에서 articleId만 추출하고 중복 제거
        val articleIds = lessonArticles.map { it.lessonArticleId.articleId }.toSet()
        // DB에서 해당 articleId에 맞는 Article 엔티티를 조회 후, ID를 key로 매핑
        val articleMap = articlePort.findAllByIds(articleIds).associateBy { it.id!! }

        // LessonArticle 도메인을 투자 차수별로 그룹핑하여 LessonArticleResponse DTO로 변환
        return lessonArticles
            .groupBy { it.investmentRound }
            .map { (round, articles) ->
                LessonArticleResponse(
                    investmentRound = round,
                    articles = articles.map { lessonArticle ->
                        val article = articleMap[lessonArticle.lessonArticleId.articleId]
                            ?: throw ArticleNotFoundException
                        ArticleResponse.from(article)
                    }
                )
            }
    }
}
