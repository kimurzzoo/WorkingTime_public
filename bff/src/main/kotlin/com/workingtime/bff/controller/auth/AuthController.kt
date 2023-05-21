package com.workingtime.bff.controller.auth

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class AuthController {

    @RequestMapping("/login")
    fun login() : String
    {
        return "auth/login"
    }

    @RequestMapping("/register")
    fun register() : String
    {
        return "auth/register"
    }

    @RequestMapping("/emailverification")
    fun emailVerification() : String
    {
        return "auth/emailverification"
    }

    @RequestMapping("/forgotpassword")
    fun forgotPassword() : String
    {
        return "auth/forgotpassword"
    }
}