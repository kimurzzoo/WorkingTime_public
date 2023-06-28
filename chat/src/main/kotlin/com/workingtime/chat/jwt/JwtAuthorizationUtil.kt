package com.workingtime.chat.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
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
        return Jwts.parserBuilder().setSigningKey(realkey).build().parseClaimsJws(bearerToken(token)).body
    }

    fun getUsername(token : String) : String
    {
        return Jwts.parserBuilder().setSigningKey(realkey).build().parseClaimsJws(bearerToken(token)).body.subject
    }

    fun validateToken(jwtToken: String?): Boolean {
        return try {
            val bearerT = bearerToken(jwtToken)
            val claims = Jwts.parserBuilder().setSigningKey(realkey).build().parseClaimsJws(bearerT)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
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