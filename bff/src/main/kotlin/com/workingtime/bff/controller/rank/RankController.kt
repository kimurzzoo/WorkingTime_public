package com.workingtime.bff.controller.rank

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/rank")
class RankController {

    @RequestMapping("")
    fun rank() : String
    {
        return "rank/rank"
    }
}