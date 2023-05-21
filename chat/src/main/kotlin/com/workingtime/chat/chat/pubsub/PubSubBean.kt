package com.workingtime.chat.chat.pubsub

import com.google.cloud.spring.pubsub.core.PubSubTemplate
import com.google.cloud.spring.pubsub.integration.AckMode
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders
import com.google.gson.Gson
import com.workingtime.chat.chat.entity.ChatMessageToClient
import com.workingtime.chat.chat.entity.ChatMessageToPubSub
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.simp.SimpMessageSendingOperations

class PubSubBean(private val messagingTemplate : SimpMessageSendingOperations) {

    private val gson = Gson()

    // Create a message channel for messages arriving from the subscription `chat-sub`.
    @Bean
    fun inputMessageChannel(): MessageChannel? {
        return PublishSubscribeChannel()
    }

    // Create an inbound channel adapter to listen to the subscription `chat-sub` and send
    // messages to the input message channel.
    @Bean
    fun inboundChannelAdapter(
        @Qualifier("inputMessageChannel") messageChannel: MessageChannel?,
        pubSubTemplate: PubSubTemplate?
    ): PubSubInboundChannelAdapter? {
        val adapter = PubSubInboundChannelAdapter(pubSubTemplate, "chat-sub")
        adapter.outputChannel = messageChannel
        adapter.ackMode = AckMode.MANUAL
        adapter.payloadType = String::class.java
        return adapter
    }

    // Define what happens to the messages arriving in the message channel.
    @ServiceActivator(inputChannel = "inputMessageChannel")
    fun messageReceiver(
        payload: String,
        @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) message: BasicAcknowledgeablePubsubMessage
    ) {
        val model = gson.fromJson(payload, ChatMessageToPubSub::class.java)
        messagingTemplate.convertAndSend("/sub/message/" + model.roomId.toString(), ChatMessageToClient(model.userId, model.message))
        //LOGGER.info("Message arrived via an inbound channel adapter from chat-sub! Payload: $payload")
        message.ack()
    }

    // Create an outbound channel adapter to send messages from the input message channel to the
    // topic `chat`.
    @Bean
    @ServiceActivator(inputChannel = "inputMessageChannel")
    fun messageSender(pubsubTemplate: PubSubTemplate?): MessageHandler? {
        val adapter = PubSubMessageHandler(pubsubTemplate, "chat")
        adapter.setSuccessCallback { ackId, message -> println(message.payload) }
        adapter.setFailureCallback { cause, message -> println(message.payload) }
        return adapter
    }
}