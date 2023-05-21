package com.workingtime.auth.account.repository.redis

import com.workingtime.auth.account.entity.RefreshToken
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, String> {
    fun findByIdAndIndicator(refreshToken : String, indicator : String) : RefreshToken?
}