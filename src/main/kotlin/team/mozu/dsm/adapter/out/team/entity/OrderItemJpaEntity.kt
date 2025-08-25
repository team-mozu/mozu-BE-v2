package team.mozu.dsm.adapter.out.team.entity

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinColumns
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.id.LessonItemId
import team.mozu.dsm.domain.team.type.OrderType
import team.mozu.dsm.global.entity.BaseTimeEntity
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_order_item")
class OrderItemJpaEntity(

    @Column(nullable = false)
    var orderType: OrderType,

    @Column(nullable = false)
    var orderCount: Int,

    @Column(nullable = false)
    var itemPrice: Long,

    @Column(nullable = false)
    var totalAmount: Long,

    @Column(nullable = false)
    var orderedAt: LocalDateTime,

    @Column(nullable = false)
    var invCnt: Int,

    /**
     * 객체 참조가 아닌 값을 직접 저장
     */
    @AttributeOverrides(
        AttributeOverride(name = "lessonId", column = Column(name = "lesson_id")),
        AttributeOverride(name = "itemId", column = Column(name = "item_id"))
    )
    @Embedded
    var lessonItemId: LessonItemId,

    /**
     * 위의 lessonItemId에 저장된 값들을 이용해서 실제 LessonItemJpaEntity 객체를 조회 및 참조
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(name = "lesson_id", referencedColumnName = "lesson_id", insertable = false, updatable = false),
        JoinColumn(name = "item_id", referencedColumnName = "item_id", insertable = false, updatable = false)
    )
    var lessonItem: LessonItemJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: TeamJpaEntity
) : BaseTimeEntity()
