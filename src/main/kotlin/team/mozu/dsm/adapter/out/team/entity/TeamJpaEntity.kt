package team.mozu.dsm.adapter.out.team.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.global.entity.BaseTimeEntity
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_lesson_team")
class TeamJpaEntity(

    @Column(columnDefinition = "VARCHAR(100)")
    var teamName: String?,

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    var schoolName: String,

    @Column(nullable = false)
    var totalMoney: Long,

    @Column(nullable = false)
    var cashMoney: Long,

    @Column(nullable = false)
    var valuationMoney: Long,

    @Column(nullable = false, columnDefinition = "VARCHAR(40)")
    var lessonNum: String,

    @Column(nullable = false)
    var isInvestmentInProgress: Boolean = false,

    @Column(nullable = false)
    var participationDate: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    var lesson: LessonJpaEntity
) : BaseTimeEntity()
