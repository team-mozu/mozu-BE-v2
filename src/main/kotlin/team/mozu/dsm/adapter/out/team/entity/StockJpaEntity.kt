package team.mozu.dsm.adapter.out.team.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import team.mozu.dsm.global.entity.BaseTimeEntity

@Entity
@Table(name = "tbl_stock")
class StockJpaEntity(

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    var itemName: String,

    @Column(nullable = false)
    var itemCount: Long,

    @Column(nullable = false)
    var buyMoney: Long,

    @Column(nullable = false)
    var valProfit: Long,

    @Column(nullable = false)
    var profitNum: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: TeamJpaEntity

) : BaseTimeEntity()
