package com.workingtime.check.check.entity

import com.workingtime.check.account.entity.BaseEntity
import com.workingtime.check.account.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "check")
class Check(
    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP")
    var startTime : LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user : User,

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    var company : Company
    ) : BaseEntity()
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id : Long? = null

    @Column(name = "end_time", nullable = true, columnDefinition = "TIMESTAMP")
    var endTime : LocalDateTime? = null
}