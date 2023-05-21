package com.workingtime.check.account.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class BaseEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    var createdDate : LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    var updatedAt : LocalDateTime = LocalDateTime.now()
}