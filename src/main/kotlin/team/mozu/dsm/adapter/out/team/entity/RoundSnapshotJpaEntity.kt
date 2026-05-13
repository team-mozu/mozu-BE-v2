package team.mozu.dsm.adapter.out.team.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.global.entity.BaseTimeEntity
import java.time.LocalDateTime

@Entity
@Table(
    name = "tbl_round_snapshot",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_round_snapshot_lesson_team_round", columnNames = ["lesson_id", "team_id", "inv_round"])
    ]
)
class RoundSnapshotJpaEntity(

    @Column(name = "inv_round", nullable = false)
    var invRound: Int,

    @Column(nullable = false)
    var totalMoney: Long,

    @Column(nullable = false)
    var cashMoney: Long,

    @Column(nullable = false)
    var valuationMoney: Long,

    @Column(nullable = false)
    var profitNum: Double,

    @Column(nullable = false)
    var endedAt: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    var lesson: LessonJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    var team: TeamJpaEntity

) : BaseTimeEntity()
