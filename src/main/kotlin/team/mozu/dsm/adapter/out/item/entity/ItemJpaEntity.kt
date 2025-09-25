package team.mozu.dsm.adapter.out.item.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_item")
@Where(clause = "is_deleted = false")
class ItemJpaEntity(
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    var itemName: String,

    @Column(columnDefinition = "VARCHAR(255)")
    var itemLogo: String?,

    @Column(nullable = false, columnDefinition = "TEXT")
    var itemInfo: String,

    @Column(nullable = false)
    var money: Long,

    @Column(nullable = false)
    var debt: Long,

    @Column(nullable = false)
    var capital: Long,

    @Column(nullable = false)
    var profit: Long,

    @Column(nullable = false)
    var profitOg: Long,

    @Column(nullable = false)
    var profitBenefit: Long,

    @Column(nullable = false)
    var netProfit: Long,

    @CreatedDate
    @Column(updatable = false, nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id", nullable = false)
    var organ: OrganJpaEntity
)
