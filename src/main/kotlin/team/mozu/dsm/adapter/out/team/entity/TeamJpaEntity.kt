package team.mozu.dsm.adapter.out.team.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import team.mozu.dsm.adapter.out.`class`.entity.ClassJpaEntity
import team.mozu.dsm.global.entity.BaseTimeEntity
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_class_team")
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
    var classNumber: String,

    @Column(nullable = false)
    var isInvestmentCompleted: Boolean = false,

    @Column(nullable = false)
    var participationDate: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    var clazz: ClassJpaEntity

) : BaseTimeEntity()
