package com.workingtime.chat.chat.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    @Autowired
    private lateinit var filterChannelInterceptor: FilterChannelInterceptor

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/sub") // sub topic
        registry.setApplicationDestinationPrefixes("/pub") // client sends messages to /pub/message
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chatting").setAllowedOrigins("https://workingtime.kro.kr").withSockJS() //ws url
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(filterChannelInterceptor)
    }
}