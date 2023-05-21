package com.workingtime.check.check.repository

import com.workingtime.check.account.entity.User
import com.workingtime.check.check.entity.Check
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CheckRepository : JpaRepository<Check, Long> {
    fun findByIdAndUser(checkId : Long, user : User) : Check?

    /*@Query("select \"check\".\"id\", \"start_time\", \"end_time\", \"company_id\", \"user_id\" from \"check\" join (select * from (select \"id\", (LAG(\"id\", 1) over ( order by \"id\" asc)) as ASDFG from \"check\") NewTable where \"id\" = :checkId) targettable on (\"check\".\"id\" = targettable.\"id\" or \"check\".\"id\" = targettable.ASDFG) where \"user_id\" = :userId order by \"check\".\"id\" asc", nativeQuery = true)
    fun findLagrowByIdAndUserId(checkId : Long, userId : Long?) : List<Check>

    @Query("select \"check\".\"id\", \"start_time\", \"end_time\", \"company_id\", \"user_id\" from \"check\" join (select * from (select \"id\", (LEAD(\"id\", 1) over ( order by \"id\" asc)) as ASDFG from \"check\") NewTable where \"id\" = :checkId) targettable on (\"check\".\"id\" = targettable.\"id\" or \"check\".\"id\" = targettable.ASDFG) where \"user_id\" = :userId order by \"check\".\"id\" asc", nativeQuery = true)
    fun findLeadrowByIdAndUserId(checkId : Long, userId : Long?) : List<Check>*/ // h2

    @Query("select `check`.id, start_time, end_time, company_id, user_id from `check` join (select * from (select id, (LAG(id, 1) over (partition by user_id order by id asc)) as ASDFG from `check`) NewTable where id = :checkId and user_id = :userId) targettable on (`check`.id = targettable.id or `check`.id = targettable.ASDFG) where user_id = :userId order by `check`.id asc", nativeQuery = true)
    fun findLagrowByIdAndUserId(checkId : Long, userId : Long?) : List<Check>

    @Query("select `check`.id, start_time, end_time, company_id, user_id from `check` join (select * from (select id, (LEAD(id, 1) over (partition by user_id order by id asc)) as ASDFG from `check`) NewTable where id = :checkId and user_id = :userId) targettable on (`check`.id = targettable.id or `check`.id = targettable.ASDFG) where user_id = :userId order by `check`.id asc", nativeQuery = true)
    fun findLeadrowByIdAndUserId(checkId : Long, userId : Long?) : List<Check> //mysql
}