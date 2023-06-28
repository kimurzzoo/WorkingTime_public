package com.workingtime.bff.controller.main

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("")
class MainController {
    //
    @RequestMapping("")
    fun mainweb() : String
    {
        return "redirect:/auth/login"
    }
}