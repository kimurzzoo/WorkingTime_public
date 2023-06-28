package com.workingtime.rank.rank.controller

import com.workingtime.rank.rank.dto.AvgMineDTO
import com.workingtime.rank.rank.dto.AvgUserWorkingTimeDTO
import com.workingtime.rank.rank.dto.TopRankWorkingTimeDTO
import com.workingtime.rank.rank.service.RankService
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rank")
class RankRestController(private val rankService: RankService) {

    @PostMapping("/avgmine")
    fun avgMine(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestBody avgMineDTO: AvgMineDTO) : AvgUserWorkingTimeDTO
    {
        if(!StringUtils.hasText(email))
        {
            return AvgUserWorkingTimeDTO(null, 400, "Bad Request")
        }
        return rankService.avgUserWorkingTime(email, avgMineDTO.companyName, avgMineDTO.startDate, avgMineDTO.endDate)
    }

    @GetMapping("/avgcompany")
    fun avgCompany(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestParam("duration") duration : String) : AvgUserWorkingTimeDTO
    {
        if(!StringUtils.hasText(email))
        {
            return AvgUserWorkingTimeDTO(null, 400, "Bad Request")
        }
        return rankService.avgMyCompanyWorkingTime(email, duration)
    }

    @GetMapping("/avgallcompany")
    fun avgAllCompany(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestParam(value = "duration", defaultValue = "all") duration : String) : AvgUserWorkingTimeDTO
    {
        if(!StringUtils.hasText(email))
        {
            return AvgUserWorkingTimeDTO(null, 400, "Bad Request")
        }
        return rankService.avgAllWorkingTime(email, duration)
    }

    @GetMapping("/avgrank")
    fun avgRank(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestParam(value = "range") range : Int, @RequestParam(value = "duration", defaultValue = "all") duration : String, @RequestParam(value = "order", defaultValue = "short") order : String) : TopRankWorkingTimeDTO
    {
        if(!StringUtils.hasText(email))
        {
            return TopRankWorkingTimeDTO(listOf(), 400, "Bad Request")
        }
        return rankService.avgTopWorkingTime(email, range, duration, order)
    }

    @GetMapping("/hb")
    fun heartbeat()
    {

    }
}