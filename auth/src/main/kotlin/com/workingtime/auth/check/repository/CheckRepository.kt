package com.workingtime.auth.check.repository

import com.workingtime.auth.check.entity.Check
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CheckRepository : JpaRepository<Check, Long>, PagingAndSortingRepository<Check, Long> {

    fun deleteByUserId(userId : Long)
}