package com.workingtime.apigateway.filter.auth

import com.workingtime.apigateway.jwt.JwtAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class LogoutGatewayFilterFactory : AbstractGatewayFilterFactory<LogoutGatewayFilterFactory.Config>(Config::class.java) {

    @Autowired
    private lateinit var jwtAuthorizationUtil: JwtAuthorizationUtil

    class Config
    {

    }

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val authorizationcookie : String = jwtAuthorizationUtil.bearerToken(exchange.request.headers.getFirst("Authorization"))
            val refreshtoken = exchange.request.cookies.getFirst("refreshtoken")?.value
            if(refreshtoken != null)
            {
                println(jwtAuthorizationUtil.getExpiration(authorizationcookie))
                println(jwtAuthorizationUtil.getExpiration(refreshtoken))
                if(jwtAuthorizationUtil.validateToken(authorizationcookie) && jwtAuthorizationUtil.validateToken(refreshtoken))
                {
                    println("success : " + jwtAuthorizationUtil.getUsername(authorizationcookie) + " " + jwtAuthorizationUtil.getUsername(refreshtoken))
                    exchange.request.mutate().header("X-Authorization-Id", jwtAuthorizationUtil.getUsername(authorizationcookie)).build()
                    exchange.request.mutate().header("indicator", jwtAuthorizationUtil.getUsername(refreshtoken)).build()
                    exchange.request.mutate().header("X-Refreshtoken", refreshtoken).build()
                }
            }
            return@GatewayFilter chain.filter(exchange)
        }
    }
}