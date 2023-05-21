package com.workingtime.auth.account.entity

import com.workingtime.auth.account.jwt.JwtTokenProvider.Companion.refreshTokenValidTime
import jakarta.persistence.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

// id : refreshtoken
@RedisHash(value = "refresh", timeToLive = (refreshTokenValidTime / 1000))
class RefreshToken(@Id @Indexed val id : String, @Indexed val indicator : String, val email: String) {
}