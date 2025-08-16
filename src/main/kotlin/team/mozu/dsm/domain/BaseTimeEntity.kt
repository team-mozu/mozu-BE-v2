package team.mozu.dsm.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity : BaseUUIDEntity() {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null
}
