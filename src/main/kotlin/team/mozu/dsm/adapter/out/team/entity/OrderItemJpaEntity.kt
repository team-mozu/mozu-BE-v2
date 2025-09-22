package team.mozu.dsm.adapter.out.team.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.domain.team.type.OrderType
import team.mozu.dsm.global.entity.BaseTimeEntity

@Entity
@Table(name = "tbl_team_order")
class OrderItemJpaEntity(

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    var itemName: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var orderType: OrderType,

    @Column(nullable = false)
    var orderCount: Int,

    @Column(nullable = false)
    var itemPrice: Int,

    @Column(nullable = false)
    var totalMoney: Int,

    @Column(nullable = false)
    var invCount: Int,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", referencedColumnName = "itemId", nullable = false, updatable = false)
    var item: ItemJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: TeamJpaEntity
) : BaseTimeEntity()
