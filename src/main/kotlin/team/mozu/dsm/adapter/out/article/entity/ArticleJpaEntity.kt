package team.mozu.dsm.adapter.out.article.entity

import jakarta.persistence.*
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.global.entity.BaseTimeEntity

@Entity
@Table(name = "tbl_article")
class ArticleJpaEntity(

    @Column(nullable = false, columnDefinition = "VARCHAR(300)")
    var articleName: String,

    @Column(name = "article_description", nullable = false, columnDefinition = "VARCHAR(10000)")
    var articleDesc: String,

    @Column(columnDefinition = "TEXT")
    var articleImage: String?,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id", nullable = false)
    var organ: OrganJpaEntity
) : BaseTimeEntity()
