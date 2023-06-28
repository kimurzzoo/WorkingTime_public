package com.workingtime.rank.rank.service

import com.workingtime.rank.account.entity.User
import com.workingtime.rank.account.repository.UserRepository
import com.workingtime.rank.check.repository.CheckRepository
import com.workingtime.rank.rank.dao.AvgRankWorkingTimeDAO
import com.workingtime.rank.rank.dao.AvgUserWorkingTimeDAO
import com.workingtime.rank.rank.dto.AvgUserWorkingTimeDTO
import com.workingtime.rank.rank.dto.RankWorkingTimeDTO
import com.workingtime.rank.rank.dto.TopRankWorkingTimeDTO
import com.workingtime.rank.util.date.DateUtil
import com.workingtime.rank.util.encryption.sym.AES256Encoder
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class RankService(private val encoder : AES256Encoder,
                  private val userRepository: UserRepository,
                    private val checkRepository: CheckRepository) {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun avgUserWorkingTime(email : String, companyName : String?, startDate : String?, endDate : String?) : AvgUserWorkingTimeDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return AvgUserWorkingTimeDTO(null, 1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return AvgUserWorkingTimeDTO(null, 1001, "you are banned")
            }

            var endDateTime = LocalDateTime.now()

            if(endDate != null)
            {
                endDateTime = DateUtil.stringToDate(endDate)
            }

            var startDateTime = endDateTime.minusWeeks(1)

            if(startDate != null)
            {
                startDateTime = DateUtil.stringToDate(startDate)
            }

            val result : AvgUserWorkingTimeDAO
            if(companyName == null)
            {
                result = checkRepository.findSumUserWorkingTime(
                    user.id,
                    startDateTime,
                    endDateTime)
            }
            else
            {
                result = checkRepository.findSumUserWorkingTime(
                    user.id,
                    companyName,
                    startDateTime,
                    endDateTime)
            }

            return if(result.getAvgtime() == null) {
                AvgUserWorkingTimeDTO(null, 4010, "there is no avarage time of this filter")
            } else {
                val avgtimestr = DateUtil.secondToDatediff(result.getAvgtime()!!)
                AvgUserWorkingTimeDTO(avgtimestr, 200, "average user working time success")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return AvgUserWorkingTimeDTO(null, 500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun avgMyCompanyWorkingTime(email : String, duration : String) : AvgUserWorkingTimeDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return AvgUserWorkingTimeDTO(null, 1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return AvgUserWorkingTimeDTO(null, 1001, "you are banned")
            }
            else if(user.company == null)
            {
                return AvgUserWorkingTimeDTO(null, 4020, "you don't work now")
            }
            else
            {
                var startDate = LocalDateTime.of(1970, 1, 1, 0, 0, 0)
                val endDate = LocalDateTime.now()
                when(duration)
                {
                    "day" -> {startDate = endDate.minusDays(1)}
                    "week" -> {startDate = endDate.minusWeeks(1)}
                    "month" -> {startDate = endDate.minusMonths(1)}
                    "year" -> {startDate = endDate.minusYears(1)}
                    "all" -> {}
                    else -> {return AvgUserWorkingTimeDTO(null, 4022, "wrong duration")}
                }
                val result : AvgUserWorkingTimeDAO = checkRepository.findAvgSumMyCompanyWorkingTime(user.company!!.name, startDate, endDate)
                return if(result.getAvgtime() == null) {
                    AvgUserWorkingTimeDTO(null, 4021, "you don't have any checks to your company")
                } else {
                    val avgtimestr = DateUtil.secondToDatediff(result.getAvgtime()!!)
                    AvgUserWorkingTimeDTO(avgtimestr, 200, "average working time of my company success")
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return AvgUserWorkingTimeDTO(null, 500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun avgAllWorkingTime(email : String, duration : String) : AvgUserWorkingTimeDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return AvgUserWorkingTimeDTO(null, 1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return AvgUserWorkingTimeDTO(null, 1001, "you are banned")
            }
            else
            {
                var startDate = LocalDateTime.of(1970, 1, 1, 0, 0, 0)
                val endDate = LocalDateTime.now()
                when(duration)
                {
                    "day" -> {startDate = endDate.minusDays(1)}
                    "week" -> {startDate = endDate.minusWeeks(1)}
                    "month" -> {startDate = endDate.minusMonths(1)}
                    "year" -> {startDate = endDate.minusYears(1)}
                    "all" -> {}
                    else -> {return AvgUserWorkingTimeDTO(null, 4032, "wrong duration")}
                }
                val result = checkRepository.findAllWorkingTime(startDate, endDate)
                return if(result.getAvgtime() == null) {
                    AvgUserWorkingTimeDTO(null, 4031, "you don't have any checks to your company")
                } else {
                    val avgtimestr = DateUtil.secondToDatediff(result.getAvgtime()!!)
                    AvgUserWorkingTimeDTO(avgtimestr, 200, "average working time of all success")
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return AvgUserWorkingTimeDTO(null, 500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun avgTopWorkingTime(email : String, range : Int, duration : String, order : String) : TopRankWorkingTimeDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return TopRankWorkingTimeDTO(listOf(), 1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return TopRankWorkingTimeDTO(listOf(), 1001, "you are banned")
            }
            else
            {
                if(range == 5 || range == 20 || range == 100)
                {
                    val pageRequest : PageRequest = PageRequest.of(0, range)
                    var startDate = LocalDateTime.of(1970, 1, 1, 0, 0, 0)
                    val endDate = LocalDateTime.now()
                    when(duration)
                    {
                        "day" -> {startDate = endDate.minusDays(1)}
                        "week" -> {startDate = endDate.minusWeeks(1)}
                        "month" -> {startDate = endDate.minusMonths(1)}
                        "year" -> {startDate = endDate.minusYears(1)}
                        "all" -> {}
                        else -> {return TopRankWorkingTimeDTO(listOf(), 4042, "wrong duration")}
                    }
                    val result : List<AvgRankWorkingTimeDAO> = when(order) {
                        "short" -> {
                            checkRepository.findTopShortestWorkingTime(startDate, endDate, pageRequest)
                        }
                        "long" -> {
                            checkRepository.findTopLongestWorkingTime(startDate, endDate, pageRequest)
                        }
                        else -> {return TopRankWorkingTimeDTO(listOf(), 4043, "wrong order") }
                    }
                    return TopRankWorkingTimeDTO(result.stream().map { ele -> RankWorkingTimeDTO(ele.getName(), DateUtil.secondToDatediff(ele.getAvgtime()!!)) }.collect(
                        Collectors.toList()), 200, "top shortest working time company success")
                }
                else
                {
                    return TopRankWorkingTimeDTO(listOf(), 4041, "wrong range")
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return TopRankWorkingTimeDTO(listOf(), 500, "exception caused")
        }
    }
}