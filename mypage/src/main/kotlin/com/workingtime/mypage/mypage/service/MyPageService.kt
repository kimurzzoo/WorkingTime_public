package com.workingtime.mypage.mypage.service

import com.workingtime.mypage.account.entity.User
import com.workingtime.mypage.account.repository.UserRepository
import com.workingtime.mypage.check.entity.Check
import com.workingtime.mypage.check.entity.Company
import com.workingtime.mypage.check.repository.CheckRepository
import com.workingtime.mypage.check.repository.CompanyRepository
import com.workingtime.mypage.mypage.dto.InfoResponseDTO
import com.workingtime.mypage.mypage.dto.MyChecksResponseDTO
import com.workingtime.mypage.util.date.DateUtil
import com.workingtime.mypage.util.encryption.sym.AES256Encoder
import com.workingtime.mypage.util.network.dto.ResponseDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class MyPageService(private val userRepository: UserRepository,
                    private val companyRepository: CompanyRepository,
                    private val checkRepository: CheckRepository,
                    private val encoder : AES256Encoder){

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun info(email : String) : InfoResponseDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return InfoResponseDTO("", "", 1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return InfoResponseDTO("", "", 1001, "you are banned")
            }
            else
            {
                val company = user.company
                var companyName : String = ""
                if(company != null)
                {
                    companyName = company.name
                }
                return InfoResponseDTO(encoder.decrypt(user.nickname), companyName, 200, "info success")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return InfoResponseDTO("", "", 500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun changeNickname(email : String, newNickname : String) : ResponseDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return ResponseDTO(1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return ResponseDTO(1001, "you are banned")
            }

            if(newNickname.length < 2 || newNickname.length > 20)
            {
                return ResponseDTO(3000, "nickname is too short or too long")
            }
            else
            {
                user.nickname = encoder.encrypt(newNickname)
                userRepository.save(user)
                return ResponseDTO(200, "change nickname success")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun changeCompany(email : String, companyName : String) : ResponseDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return ResponseDTO(1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return ResponseDTO(1001, "you are banned")
            }

            var company : Company? = companyRepository.findByName(companyName)
            if(company == null)
            {
                company = companyRepository.save(Company(companyName))
            }
            else if(!company.enabled)
            {
                return ResponseDTO(1002, "this company is shut down")
            }

            if(user.checks.isNotEmpty())
            {
                val nowCheck = user.checks[0]
                if(nowCheck.endTime == null)
                {
                    checkRepository.deleteById(nowCheck.id!!)
                }
            }

            user.company = company
            userRepository.save(user)
            return ResponseDTO(200, "change company success")
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun myChecks(email : String, startDate : String?, endDate : String?, companyName : String?, minWorkingHour : Int?, maxWorkingHour : Int?, pageNum : Int) : MyChecksResponseDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return MyChecksResponseDTO(0, listOf(), 1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return MyChecksResponseDTO(0, listOf(), 1001, "you are banned")
            }

            var startDateTime : LocalDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 1)
            var endDateTime : LocalDateTime = LocalDateTime.now()

            if(startDate != null)
            {
                startDateTime = DateUtil.stringToDate(startDate)
            }

            if(endDate != null)
            {
                endDateTime = DateUtil.stringToDate(endDate)
            }

            if(startDateTime.isAfter(endDateTime))
            {
                return MyChecksResponseDTO(0, listOf(), 3010, "end date is faster than start date")
            }

            var realCompanyName : String? = companyName
            if(realCompanyName == null)
            {
                realCompanyName = ""
            }

            var minWorkingHourData : Int = 0
            var maxWorkingHourData : Int = 2100000000

            if(minWorkingHour != null)
            {
                minWorkingHourData = minWorkingHour
            }

            if(maxWorkingHour != null)
            {
                maxWorkingHourData = maxWorkingHour
            }
            val pageable = PageRequest.of(pageNum, 20)
            val checks : Page<Check> = checkRepository.findChecksByFilters(user.id!!, startDateTime, endDateTime, realCompanyName, minWorkingHourData, maxWorkingHourData, pageable)
            val ret  = checks.content.stream().map { check -> check.toCheckDTO()}.collect(Collectors.toList())
            return MyChecksResponseDTO(checks.totalPages,
                ret, 200, "my checks success")
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return MyChecksResponseDTO(0, listOf(), 500, "exception caused")
        }
    }
}