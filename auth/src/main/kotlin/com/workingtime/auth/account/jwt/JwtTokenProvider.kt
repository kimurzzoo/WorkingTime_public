package com.workingtime.auth.account.jwt

import com.workingtime.auth.util.encryption.hash.BCryptPasswordEncoder
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider {

    @Value("\${auth.secret}")
    private var secretkey : String = ""

    companion object
    {
        const val tokenValidTime = 30 * 60 * 1000L
        const val refreshTokenValidTime = 7 * 24 * 60 * 60 * 1000L
    }

    private var realkey : Key? = null

    @PostConstruct
    protected fun init() {
        realkey = Keys.hmacShaKeyFor(secretkey.toByteArray())
    }

    fun createToken(username : String, role : String): String {
        val claims: Claims = Jwts.claims().setSubject(username) // JWT payload 에 저장되는 정보단위
        claims["role"] = role
        val now = Date()
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg", "HS256")
            .setClaims(claims) // 정보 저장
            .setIssuedAt(now) // 토큰 발행 시간 정보
            .setExpiration(Date(now.time + tokenValidTime)) // set Expire Time
            .signWith(realkey, SignatureAlgorithm.HS256)
            // signature 에 들어갈 secret값 세팅
            .compact()
    }

    fun createRefreshToken(indicator : String) : String
    {
        val claims: Claims = Jwts.claims().setSubject(indicator)
        val now = Date()

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg", "HS256")
            .setClaims(claims) // 정보 저장
            .setExpiration(Date(now.time + refreshTokenValidTime))
            .signWith(realkey , SignatureAlgorithm.HS256)
            .compact()
    }
}