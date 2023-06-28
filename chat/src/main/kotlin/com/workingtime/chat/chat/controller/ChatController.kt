package com.workingtime.chat.chat.controller

import com.workingtime.chat.chat.entity.ChatMessage
import com.workingtime.chat.chat.service.ChatService
import com.workingtime.chat.jwt.JwtAuthorizationUtil
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController // mapping /pub
class ChatController(private val chatService: ChatService,
                    private val jwtAuthorizationUtil: JwtAuthorizationUtil) {

    @MessageMapping("/message/{roomId}")
    fun message(message : ChatMessage, messageHeaderAccessor: SimpMessageHeaderAccessor, @DestinationVariable("roomId") roomId : Long)
    {
        chatService.sendMessage(jwtAuthorizationUtil.getUsername(messageHeaderAccessor.getFirstNativeHeader("Authorization")!!), message, roomId)
    }
}