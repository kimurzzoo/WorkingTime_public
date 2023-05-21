package com.workingtime.chat.check.repository

import com.workingtime.chat.check.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company,  Long> {
}