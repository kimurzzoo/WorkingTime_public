package com.workingtime.apigateway.filter.auth

import com.workingtime.apigateway.jwt.JwtAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class ReissueGatewayFilterFactory : AbstractGatewayFilterFactory<ReissueGatewayFilterFactory.Config>(
    Config::class.java)  {

    @Autowired
    private lateinit var jwtAuthorizationUtil: JwtAuthorizationUtil

    class Config
    {

    }

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val refreshtoken = exchange.request.cookies.getFirst("refreshtoken")?.value
            if(refreshtoken != null)
            {
                if(jwtAuthorizationUtil.validateToken(refreshtoken))
                {
                    exchange.request.mutate().header("indicator", jwtAuthorizationUtil.getUsername(refreshtoken)).build()
                    exchange.request.mutate().header("X-Refreshtoken", refreshtoken).build()
                }
            }
            return@GatewayFilter chain.filter(exchange)
        }
    }
}