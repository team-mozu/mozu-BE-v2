package team.mozu.dsm.domain

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.domain.Persistable
import java.util.*

@MappedSuperclass
abstract class BaseUUIDEntity(
    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @get:JvmName("getIdForJvm")
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    var id: UUID?
) : Persistable<UUID> {
    override fun getId(): UUID? = this.id

    override fun isNew(): Boolean = this.id == null
}
