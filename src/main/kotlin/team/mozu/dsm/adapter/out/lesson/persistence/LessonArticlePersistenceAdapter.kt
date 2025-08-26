package team.mozu.dsm.adapter.out.lesson.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.article.persistence.repository.ArticleRepository
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonArticleMapper
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonArticleRepository
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.application.exception.article.ArticleNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.out.lesson.CommandLessonArticlePort
import team.mozu.dsm.domain.lesson.model.LessonArticle
import java.util.UUID

@Component
class LessonArticlePersistenceAdapter(
    private val lessonRepository: LessonRepository,
    private val articleRepository: ArticleRepository,
    private val lessonArticleMapper: LessonArticleMapper,
    private val lessonArticleRepository: LessonArticleRepository
) : CommandLessonArticlePort {

    //--Query--//

    //--Command--//
    override fun saveAll(id: UUID, lessonArticles: List<LessonArticle>): List<LessonArticle> {
        val lessonEntity = lessonRepository.findById(id)
            .orElseThrow { LessonNotFoundException }

        /**
         * LessonArticleList 저장 프로세스
         * 1) lessonArticles에서 모든 articleId 추출 후 DB에서 한 번에 조회 (DB 성능 최적화를 위해)
         * 2) .associateBy를 사용하여 Map<UUID, ArticleJpaEntity> 형식으로 변환
         * 3) 각 LessonArticle 도메인 모델을 Lesson, Article과 매핑하여 JPA 엔티티 생성
         * 4) 생성된 엔티티를 saveAll로 저장 후 도메인 모델로 변환
         **/
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
}
