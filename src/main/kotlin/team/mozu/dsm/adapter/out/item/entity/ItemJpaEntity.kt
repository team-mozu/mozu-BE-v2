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
    var id: Int?,

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    var itemName: String,

    @Column(columnDefinition = "VARCHAR(255)")
    var itemLogo: String?,

    @Column(nullable = false, columnDefinition = "TEXT")
    var itemInfo: String,

    @Column(nullable = false)
    var money: Int,

    @Column(nullable = false)
    var debt: Int,

    @Column(nullable = false)
    var capital: Int,

    @Column(nullable = false)
    var profit: Int,

    @Column(nullable = false)
    var profitOg: Int,

    @Column(nullable = false)
    var profitBenefit: Int,

    @Column(nullable = false)
    var netProfit: Int,

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
