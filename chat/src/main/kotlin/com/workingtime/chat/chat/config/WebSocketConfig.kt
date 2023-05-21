package com.workingtime.chat.chat.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/sub") // sub topic
        registry.setApplicationDestinationPrefixes("/pub") // client sends messages to /pub/message
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chatting").setAllowedOrigins("*").withSockJS() //ws url
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(FilterChannelInterceptor())
    }
}