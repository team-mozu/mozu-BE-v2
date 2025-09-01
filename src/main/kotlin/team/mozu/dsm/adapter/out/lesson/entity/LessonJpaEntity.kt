package team.mozu.dsm.adapter.out.lesson.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.global.entity.BaseTimeEntity

@Entity
@Table(name = "tbl_lesson")
class LessonJpaEntity(

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    var lessonName: String,

    @Column(nullable = false)
    var maxInvRound: Int,

    @Column(nullable = false)
    var curInvRound: Int,

    @Column(nullable = false)
    var baseMoney: Int,

    @Column(unique = true, columnDefinition = "VARCHAR(10)")
    var lessonNum: String?,

    @Column(nullable = false)
    var isStarred: Boolean = false,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @Column(nullable = false)
    var isInProgress: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id", nullable = false)
    var organ: OrganJpaEntity
) : BaseTimeEntity()
