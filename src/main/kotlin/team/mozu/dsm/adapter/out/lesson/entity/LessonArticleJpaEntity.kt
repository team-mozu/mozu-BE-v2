package team.mozu.dsm.adapter.out.lesson.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.EmbeddedId
import jakarta.persistence.MapsId
import jakarta.persistence.ManyToOne
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Column
import team.mozu.dsm.adapter.out.article.entity.ArticleJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonArticleId

@Entity
@Table(name = "tbl_lesson_article")
class LessonArticleJpaEntity(

    @EmbeddedId
    var lessonArticleId: LessonArticleId,

    @MapsId("lessonId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    var lesson: LessonJpaEntity,

    @MapsId("articleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    var article: ArticleJpaEntity,

    @Column(nullable = false)
    var investmentRound: Int
)
