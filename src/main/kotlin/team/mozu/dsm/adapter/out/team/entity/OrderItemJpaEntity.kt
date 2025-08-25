package team.mozu.dsm.adapter.out.team.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinColumns
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.domain.team.type.OrderType
import team.mozu.dsm.global.entity.BaseTimeEntity
import java.time.LocalDateTime

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
    var itemPrice: Long,

    @Column(nullable = false)
    var totalAmount: Long,

    @Column(nullable = false)
    var invCount: Int,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns(
        JoinColumn(name = "lesson_id", referencedColumnName = "lesson_id", nullable = false),
        JoinColumn(name = "item_id", referencedColumnName = "item_id", nullable = false)
    )
    var lessonItem: LessonItemJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: TeamJpaEntity
) : BaseTimeEntity()
