package team.mozu.dsm.adapter.out.article.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import org.hibernate.annotations.Where
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.global.entity.BaseTimeEntity

@Entity
@Table(name = "tbl_article")
@Where(clause = "is_deleted = false")
class ArticleJpaEntity(

    @Column(nullable = false, columnDefinition = "VARCHAR(300)")
    var articleName: String,

    @Column(name = "article_description", nullable = false, columnDefinition = "VARCHAR(10000)")
    var articleDesc: String,
    // text로 변경하기

    @Column(columnDefinition = "TEXT")
    var articleImage: String?,
    // varchar(255)로 변경하기

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id", nullable = false)
    var organ: OrganJpaEntity
) : BaseTimeEntity()
