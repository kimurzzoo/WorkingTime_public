package com.workingtime.mypage.check.entity

import com.workingtime.mypage.account.entity.BaseEntity
import com.workingtime.mypage.account.entity.User
import com.workingtime.mypage.mypage.dto.CheckDTO
import com.workingtime.mypage.util.date.DateUtil
import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

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

    fun toCheckDTO() : CheckDTO
    {
        val duration = Duration.between(startTime, endTime).toSeconds()
        val hours = duration / 3600
        val minutes = (duration - hours * 3600) / 60
        val seconds = duration - hours * 3600 - minutes * 60
        return CheckDTO(id, DateUtil.dateToString(startTime)!!, DateUtil.dateToString(endTime), String.format("%d:%d:%d", hours, minutes, seconds), company.name)
    }
}