package com.workingtime.apigateway.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class JwtAuthorizationUtil {

    @Value("\${auth.secret}")
    private var secretkey : String = ""

    private var realkey : Key? = null

    @PostConstruct
    protected fun init() {
        realkey = Keys.hmacShaKeyFor(secretkey.toByteArray())
    }

    fun getClaims(token: String) : Claims
    {
        return Jwts.parserBuilder().setSigningKey(realkey).build().parseClaimsJws(token).body
    }

    fun getUsername(token : String) : String
    {
        return Jwts.parserBuilder().setSigningKey(realkey).build().parseClaimsJws(token).body.subject
    }

    fun validateToken(jwtToken: String?): Boolean {
        return try {
            val claims = Jwts.parserBuilder().setSigningKey(realkey).build().parseClaimsJws(jwtToken)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun getExpiration(jwtToken : String) : String
    {
        val claims = Jwts.parserBuilder().setSigningKey(realkey).build().parseClaimsJws(jwtToken)
        return Timestamp(claims.body.expiration.time).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy MM dd HH mm ss"))
    }

    fun bearerToken(jwt : String?) : String
    {
        try {
            if(jwt == null)
            {
                return ""
            }
            else
            {
                if(jwt.substring(0, 7) == "Bearer ")
                {
                    return jwt.substring(7)
                }
                else
                {
                    return ""
                }
            }
        }
        catch (e : Exception)
        {
            return ""
        }
    }
}