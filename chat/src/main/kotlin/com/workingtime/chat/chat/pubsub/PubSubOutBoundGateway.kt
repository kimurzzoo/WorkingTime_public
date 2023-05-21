package com.workingtime.chat.chat.pubsub

import org.springframework.integration.annotation.MessagingGateway

@MessagingGateway(defaultRequestChannel = "inputMessageChannel")
interface PubSubOutBoundGateway {
    fun sendToPubsub(text : String)
}