package com.workingtime.bff.controller.check

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/check")
class CheckController {

    @RequestMapping("/check")
    fun check() : String
    {
        return "check/check"
    }

    @RequestMapping("/mycheck")
    fun myCheck() : String
    {
        return "check/mycheck"
    }
}