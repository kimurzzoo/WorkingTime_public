package com.workingtime.auth.account.repository

import com.workingtime.auth.account.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmailAddress(emailAddress : String) : User?

    fun existsByEmailAddress(emailAddress: String) : Boolean

    fun deleteByEmailAddress(emailAddress: String)
}