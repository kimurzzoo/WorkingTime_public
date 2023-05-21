package com.workingtime.check.account.repository

import com.workingtime.check.account.entity.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    @EntityGraph(attributePaths = ["company"])
    fun findByEmailAddress(emailAddress : String) : User?
}