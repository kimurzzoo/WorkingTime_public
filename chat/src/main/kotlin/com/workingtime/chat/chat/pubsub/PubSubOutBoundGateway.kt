package com.workingtime.chat.chat.pubsub

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.messaging.MessageHandlingException
import org.springframework.messaging.MessagingException
import org.springframework.messaging.handler.annotation.Header

@MessagingGateway(defaultRequestTimeout = "500", defaultRequestChannel = "outputMessageChannel")
interface PubSubOutBoundGateway {
    @kotlin.jvm.Throws(MessagingException::class, MessageHandlingException::class)
    fun sendToPubsub(text : String)
}