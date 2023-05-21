package com.workingtime.check.check.repository

import com.workingtime.check.check.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company,  Long> {
}