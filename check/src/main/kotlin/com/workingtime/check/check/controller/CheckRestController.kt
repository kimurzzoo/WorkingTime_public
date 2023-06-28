package com.workingtime.check.check.controller

import com.workingtime.check.check.dto.NowCheckResponseDTO
import com.workingtime.check.check.service.CheckService
import com.workingtime.check.util.network.dto.ResponseDTO
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/check")
class CheckRestController(private val checkService: CheckService) {

    @GetMapping("/startcheck")
    fun startCheck(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String) : ResponseDTO
    {
        if(!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }
        return checkService.startCheck(email)
    }

    @GetMapping("/endcheck")
    fun endCheck(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String) : ResponseDTO
    {
        if(!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }
        return checkService.endCheck(email)
    }

    @GetMapping("/modifystarttime")
    fun modifyStartTime(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestParam(name = "checkid") checkId : Long, @RequestParam(name = "modifiedtime") modifiedTime : String) : ResponseDTO
    {
        if(!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }
        return checkService.modifyStartTime(email, checkId, modifiedTime)
    }

    @GetMapping("/modifyendtime")
    fun modifyEndTime(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestParam(name = "checkid") checkId : Long, @RequestParam(name = "modifiedtime") modifiedTime : String) : ResponseDTO
    {
        if(!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }
        return checkService.modifyEndTime(email, checkId, modifiedTime)
    }

    @GetMapping("/deletecheck")
    fun deleteCheck(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestParam(name = "checkid") checkId : Long) : ResponseDTO
    {
        if(!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }
        return checkService.deleteCheck(email, checkId)
    }

    @GetMapping("/nowcheck")
    fun nowCheck(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String) : NowCheckResponseDTO
    {
        if(!StringUtils.hasText(email))
        {
            return NowCheckResponseDTO(null, 400, "Bad Request")
        }
        return checkService.nowCheck(email)
    }

    @GetMapping("/hb")
    fun heartbeat()
    {

    }
}