package com.workingtime.chat.chat.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chat")
class HeartbeatController {

    @GetMapping("/hb")
    fun heartbeat()
    {

    }

}