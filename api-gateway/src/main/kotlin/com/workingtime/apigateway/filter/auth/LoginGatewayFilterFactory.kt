package com.workingtime.apigateway.filter.auth

import com.workingtime.apigateway.jwt.JwtAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class LoginGatewayFilterFactory : AbstractGatewayFilterFactory<LoginGatewayFilterFactory.Config>(Config::class.java){

    @Autowired
    private lateinit var jwtAuthorizationUtil: JwtAuthorizationUtil

    class Config {
    }

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val token : String = jwtAuthorizationUtil.bearerToken(exchange.request.headers.getFirst("Authorization"))
            if(jwtAuthorizationUtil.validateToken(token))
            {
                addAuthorizationHeaders(exchange.request, jwtAuthorizationUtil.getUsername(token))
            }
            return@GatewayFilter chain.filter(exchange)
        }
    }

    private fun addAuthorizationHeaders(request : ServerHttpRequest, username : String)
    {
        request.mutate().header("X-Authorization-Id", username).build()
    }
}