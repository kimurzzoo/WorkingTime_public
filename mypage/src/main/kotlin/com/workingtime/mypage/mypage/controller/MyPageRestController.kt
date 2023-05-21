package com.workingtime.mypage.mypage.controller

import com.workingtime.mypage.mypage.dto.MyChecksDTO
import com.workingtime.mypage.mypage.dto.MyChecksResponseDTO
import com.workingtime.mypage.mypage.service.MyPageService
import com.workingtime.mypage.util.network.dto.ResponseDTO
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mypage")
class MyPageRestController(private val myPageService: MyPageService) {

    @GetMapping("/changenickname")
    fun changeNickname(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestParam(value = "newnickname", defaultValue = "") newNickname : String) : ResponseDTO
    {
        if(!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }
        return myPageService.changeNickname(email, newNickname)
    }

    @GetMapping("/changecompany")
    fun changeCompany(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestParam(value = "companyname", defaultValue = "") companyName : String) : ResponseDTO
    {
        if(!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }
        return myPageService.changeCompany(email, companyName)
    }

    @PostMapping("/mychecks")
    fun myChecks(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestBody myChecksDTO: MyChecksDTO) : MyChecksResponseDTO
    {
        if(!StringUtils.hasText(email))
        {
            return MyChecksResponseDTO(0, listOf(), 400, "Bad Request")
        }
        return myPageService.myChecks(email, myChecksDTO.startDate, myChecksDTO.endDate, myChecksDTO.companyName, myChecksDTO.minWorkingHour, myChecksDTO.maxWorkingHour, myChecksDTO.pageNum)
    }
}