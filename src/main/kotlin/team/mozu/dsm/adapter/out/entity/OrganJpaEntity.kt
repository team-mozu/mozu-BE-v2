package team.mozu.dsm.adapter.out.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import team.mozu.dsm.domain.BaseUUIDEntity

@Entity
@Table(name = "tbl_organ") // JPQL은 도메인 모델 네이밍을 유지하고, SQL은 DB 스키마 변경에만 영향을 받도록 설계
class OrganJpaEntity(
    organCode: String,
    organName: String,
    password: String
) : BaseUUIDEntity() {
    @Column(columnDefinition = "VARCHAR(5)", nullable = false, unique = true)
    var organCode: String = organCode
        protected set

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    var organName: String = organName
        protected set

    @Column(columnDefinition = "VARCHAR(300)", nullable = false)
    var password: String = password
        protected set
}
