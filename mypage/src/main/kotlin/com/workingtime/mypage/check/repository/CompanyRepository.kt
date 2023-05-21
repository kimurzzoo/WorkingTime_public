package com.workingtime.mypage.check.repository

import com.workingtime.mypage.check.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company,  Long> {
    fun findByName(name : String) : Company?
}