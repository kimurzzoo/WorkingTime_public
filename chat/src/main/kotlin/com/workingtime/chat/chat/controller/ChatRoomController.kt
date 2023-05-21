package com.workingtime.chat.chat.controller

import com.workingtime.chat.chat.entity.ChatRoom
import com.workingtime.chat.chat.service.ChatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chatroom")
class ChatRoomController(private val chatService: ChatService) {
    @GetMapping("")
    fun roomInfo(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String) : ChatRoom?
    {
        return chatService.roomInfo(email)
    }
}