package team.mozu.dsm.adapter.out.lesson.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundArticleResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.QLessonRoundArticleResponse
import team.mozu.dsm.adapter.out.article.persistence.repository.ArticleRepository
import team.mozu.dsm.adapter.out.lesson.entity.QLessonArticleJpaEntity.lessonArticleJpaEntity
import team.mozu.dsm.adapter.out.article.entity.QArticleJpaEntity.articleJpaEntity
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonArticleMapper
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonArticleRepository
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.out.lesson.CommandLessonArticlePort
import team.mozu.dsm.application.port.out.lesson.QueryLessonArticlePort
import team.mozu.dsm.domain.lesson.model.LessonArticle
import java.util.UUID

@Component
class LessonArticlePersistenceAdapter(
    private val lessonRepository: LessonRepository,
    private val articleRepository: ArticleRepository,
    private val lessonArticleMapper: LessonArticleMapper,
    private val lessonArticleRepository: LessonArticleRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : QueryLessonArticlePort, CommandLessonArticlePort {

    //--Query--//
    override fun findAllByLessonId(lessonId: UUID): List<LessonArticle> {
        return lessonArticleRepository.findAllByLessonId(lessonId)
            .map { lessonArticleMapper.toModel(it) }
    }

    override fun findAllRoundArticlesByLessonId(lessonId: UUID, nowInvRound: Int): List<LessonRoundArticleResponse> {
        return jpaQueryFactory
            .select(
                QLessonRoundArticleResponse(
                    articleJpaEntity.id,
                    articleJpaEntity.articleName,
                    articleJpaEntity.articleDesc,
                    articleJpaEntity.articleImage
                )
            )
            .from(lessonArticleJpaEntity)
            .join(lessonArticleJpaEntity.article, articleJpaEntity)
            .where(
                lessonArticleJpaEntity.lesson.id.eq(lessonId)
                    .and(lessonArticleJpaEntity.investmentRound.eq(nowInvRound))
            )
            .fetch()
    }

    //--Command--//
    override fun saveAll(id: UUID, lessonArticles: List<LessonArticle>): List<LessonArticle> {
        val lessonEntity = lessonRepository.findById(id).orElse(null)
            ?: throw LessonNotFoundException

        /**
         * LessonArticleList 저장 프로세스
         * 1) lessonArticles에서 모든 articleId 추출 후 DB에서 한 번에 조회 (DB 성능 최적화를 위해)
         * 2) .associateBy를 사용하여 Map<UUID, ArticleJpaEntity> 형식으로 변환
         * 3) 각 LessonArticle 도메인 모델을 Lesson, Article과 매핑하여 JPA 엔티티 생성
         * 4) 생성된 엔티티를 saveAll로 저장 후 도메인 모델로 변환
         */
        val lessonArticleList = articleRepository.findAllById(lessonArticles.map { it.lessonArticleId.articleId })
            .associateBy { it.id }
            .let { articleMap ->
                lessonArticles.map { model ->
                    val articleEntity = articleMap[model.lessonArticleId.articleId]
                        ?: throw ArticleNotFoundException

                    lessonArticleMapper.toEntity(model, lessonEntity, articleEntity)
                }
            }
        lessonArticleRepository.saveAll(lessonArticleList)

        return lessonArticleList.map { entity -> lessonArticleMapper.toModel(entity) }
    }

    override fun deleteAll(lessonId: UUID) {
        // lessonId 기준으로 기존 LessonArticle 전부 삭제
        jpaQueryFactory.delete(lessonArticleJpaEntity)
            .where(lessonArticleJpaEntity.lesson.id.eq(lessonId))
            .execute()
    }
}
