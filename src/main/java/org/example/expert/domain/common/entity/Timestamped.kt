package org.example.expert.domain.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class KTimestamped {

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(updatable = true)
    var modifiedAt: LocalDateTime? = null
}