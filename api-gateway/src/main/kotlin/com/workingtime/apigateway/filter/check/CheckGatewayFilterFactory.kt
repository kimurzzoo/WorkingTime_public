package com.workingtime.apigateway.filter.check

import com.workingtime.apigateway.jwt.JwtAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class CheckGatewayFilterFactory : AbstractGatewayFilterFactory<CheckGatewayFilterFactory.Config>(
    CheckGatewayFilterFactory.Config::class.java){

    @Autowired
    private lateinit var jwtAuthorizationUtil: JwtAuthorizationUtil

    data class Config(
        val role_verifieduser: String?,
        val role_admin : String?,
        val role_superadmin : String?
    )

    override fun apply(config: Config?): GatewayFilter {
        val configrole : List<String?> = listOf(config!!.role_verifieduser, config.role_admin, config.role_superadmin)
        return GatewayFilter { exchange, chain ->
            val token : String = jwtAuthorizationUtil.bearerToken(exchange.request.headers.getFirst("Authorization"))
            if(jwtAuthorizationUtil.validateToken(token))
            {
                val userInfo = jwtAuthorizationUtil.getClaims(token) // 파싱된 토큰의 claim을 추출해 아이디 값을 가져온다.
                addAuthorizationHeaders(exchange.request, jwtAuthorizationUtil.getUsername(token), userInfo["role"] as String, configrole)
            }
            return@GatewayFilter chain.filter(exchange)
        }
    }

    private fun addAuthorizationHeaders(request : ServerHttpRequest, username : String, role : String, configrole : List<String?>)
    {
        if(configrole.contains(role))
        {
            request.mutate().header("X-Authorization-Id", username).build()
        }
    }

    override fun shortcutFieldOrder(): List<String?>? {
        // we need this to use shortcuts in the application.yml
        return listOf("role_verifieduser", "role_admin", "role_superadmin")
    }
}