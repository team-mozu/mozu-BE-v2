package team.mozu.dsm.adapter.out.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import team.mozu.dsm.domain.BaseUUIDEntity

@Entity
@Table(name = "tbl_organ")
class OrganJpaEntity(

    @Column(columnDefinition = "VARCHAR(5)", nullable = false, unique = true)
    var organCode: String,

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    var organName: String,

    @Column(columnDefinition = "VARCHAR(300)", nullable = false)
    var password: String
) : BaseUUIDEntity()
