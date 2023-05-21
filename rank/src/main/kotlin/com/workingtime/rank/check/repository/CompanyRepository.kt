package com.workingtime.rank.check.repository

import com.workingtime.rank.check.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company,  Long> {
}