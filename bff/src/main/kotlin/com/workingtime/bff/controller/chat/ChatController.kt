package com.workingtime.bff.controller.chat

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/chat")
class ChatController {

    @RequestMapping("")
    fun chat() : String
    {
        return "chat/chat"
    }
}