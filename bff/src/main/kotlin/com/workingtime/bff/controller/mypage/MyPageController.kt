package com.workingtime.bff.controller.mypage

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/mypage")
class MyPageController {
    @RequestMapping("")
    fun myPage() : String
    {
        return "mypage/mypage"
    }
}