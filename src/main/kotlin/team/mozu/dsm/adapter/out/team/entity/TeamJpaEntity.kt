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

    @Column(name = "team_name", columnDefinition = "VARCHAR(100)")
    var teamName: String?,

    @Column(name = "school_name", nullable = false, columnDefinition = "VARCHAR(100)")
    var schoolName: String,

    @Column(name = "total_money", nullable = false)
    var totalMoney: Int,

    @Column(name = "cash_money", nullable = false)
    var cashMoney: Int,

    @Column(name = "valuation_money", nullable = false)
    var valuationMoney: Int,

    @Column(name = "class_number", nullable = false, columnDefinition = "VARCHAR(40)")
    var classNumber: String,

    @Column(name = "investment_complete_yn", nullable = false)
    var investmentCompleteYn: Boolean = false,

    @Column(name = "participation_date", nullable = false)
    var participationDate: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    var clazz: ClassJpaEntity

) : BaseTimeEntity()
