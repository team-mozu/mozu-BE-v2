package team.mozu.dsm.adapter.out.lesson.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.article.entity.ArticleJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.LessonArticleJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonArticleId
import team.mozu.dsm.domain.lesson.model.LessonArticle

@Mapper(componentModel = "spring")
abstract class LessonArticleMapper {

    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "articleId", source = "article.id")
    abstract fun toModel(entity: LessonArticleJpaEntity): LessonArticle

    fun toEntity(model: LessonArticle, lesson: LessonJpaEntity, article: ArticleJpaEntity): LessonArticleJpaEntity {
        return LessonArticleJpaEntity(
            lessonArticleId = LessonArticleId(model.lessonId, model.articleId),
            lesson = lesson,
            article = article,
            investmentRound = model.investmentRound
        )
    }
}
