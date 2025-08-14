package team.mozu.dsm.infrastructure.persistence.organ

import jakarta.persistence.Column
import jakarta.persistence.Entity
import team.mozu.dsm.domain.BaseUUIDEntity
import java.util.UUID

@Entity(name = "tb_admin")
class OrganJpaEntity(
    id: UUID?,
    adminCode: String,
    organName: String,
    password: String
) : BaseUUIDEntity(id) {
    @Column(name = "admin_id", length = 5, nullable = false, unique = true)
    var adminCode: String = adminCode
        protected set

    @Column(name = "org_name", length = 100, nullable = false)
    var organName: String = organName
        protected set

    @Column(name = "password", length = 300, nullable = false)
    var password: String = password
        protected set
}
