package com.workingtime.check.check.service

import com.workingtime.check.account.entity.User
import com.workingtime.check.account.repository.UserRepository
import com.workingtime.check.check.dto.NowCheckDTO
import com.workingtime.check.check.dto.NowCheckResponseDTO
import com.workingtime.check.check.entity.Check
import com.workingtime.check.check.repository.CheckRepository
import com.workingtime.check.util.date.DateUtil
import com.workingtime.check.util.encryption.sym.AES256Encoder
import com.workingtime.check.util.network.dto.ResponseDTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CheckService(private val userRepository : UserRepository,
                    private val checkRepository: CheckRepository,
                   private val encoder : AES256Encoder) {

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun startCheck(email : String) : ResponseDTO
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
            else if(user.company == null)
            {
                return ResponseDTO(2011, "you don't work now")
            }
            else
            {
                if(user.checks.isNotEmpty())
                {
                    if(user.checks[0].endTime == null)
                    {
                        return ResponseDTO(2012, "please end the previous check")
                    }
                }
                checkRepository.save(Check(LocalDateTime.now(), user, user.company!!))
                return ResponseDTO(200, "start check success")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun endCheck(email : String) : ResponseDTO
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
            else
            {
                if(user.checks.isNotEmpty())
                {
                    if(user.checks[0].endTime == null)
                    {
                        user.checks[0].endTime = LocalDateTime.now()
                        userRepository.save(user)
                        return ResponseDTO(200, "end check success")
                    }
                    else
                    {
                        return ResponseDTO(2011, "already ended")
                    }
                }
                else
                {
                    return ResponseDTO(2012, "there is no check")
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun modifyStartTime(email : String, checkId : Long, modifiedTime: String) : ResponseDTO
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

            val checks : List<Check> = checkRepository.findLagrowByIdAndUserId(checkId, user.id)
            var check : Check? = null
            val modifiedTimeDate = DateUtil.stringToDate(modifiedTime)
            if(checks.isEmpty())
            {
                return ResponseDTO(2021, "there is no check for this id and user")
            }
            else
            {
                check = checks.last()
            }
            if(modifiedTimeDate.isAfter(check.endTime))
            {
                return ResponseDTO(2022, "start time is later than end time")
            }
            else
            {
                var previousCheck : Check? = null
                if(checks.size > 1)
                {
                    previousCheck = checks[0]
                }

                if(previousCheck != null)
                {
                    if(previousCheck.endTime == null)
                    {
                        return ResponseDTO(2024, "fatal error : you created new check before end the previous one")
                    }
                    else if(previousCheck.endTime!!.isAfter(modifiedTimeDate))
                    {
                        return ResponseDTO(2025, "the end time of your last check is later than this start time")
                    }
                }
                check.startTime = modifiedTimeDate
                checkRepository.save(check)
                return ResponseDTO(200, "modify start time success")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun modifyEndTime(email : String, checkId : Long, modifiedTime: String) : ResponseDTO
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

            val checks : List<Check> = checkRepository.findLeadrowByIdAndUserId(checkId, user.id)
            var check : Check? = null
            val modifiedTimeDate = DateUtil.stringToDate(modifiedTime)
            if(checks.isEmpty())
            {
                return ResponseDTO(2031, "there is no check for this id and user")
            }
            else
            {
                check = checks[0]
            }

            if(modifiedTimeDate.isBefore(check.startTime))
            {
                return ResponseDTO(2032, "end time is faster than start time")
            }
            else if(modifiedTimeDate.isAfter(LocalDateTime.now()))
            {
                return ResponseDTO(2034, "end time is later than now")
            }
            else
            {
                var latercheck : Check? = null
                if(checks.size > 1)
                {
                    latercheck = checks[1]
                }
                if(latercheck != null)
                {
                    if(latercheck.startTime.isBefore(modifiedTimeDate))
                    {
                        return ResponseDTO(2033, "the start time of your last check is later than this start time")
                    }
                }
                check.endTime = modifiedTimeDate
                checkRepository.save(check)
                return ResponseDTO(200, "modify end time success")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun deleteCheck(email : String, checkId: Long) : ResponseDTO
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

            checkRepository.deleteById(checkId)
            return ResponseDTO(200, "delete check success")
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun nowCheck(email : String) : NowCheckResponseDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return NowCheckResponseDTO(null,1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return NowCheckResponseDTO(null,1001, "you are banned")
            }

            if(user.checks.isEmpty())
            {
                return NowCheckResponseDTO(null,2051, "there is no check of yours")
            }
            val nowCheck = user.checks[0]
            return NowCheckResponseDTO(NowCheckDTO(nowCheck.id, DateUtil.dateToString(nowCheck.startTime)!!, DateUtil.dateToString(nowCheck.endTime)), 200, "now check success")
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return NowCheckResponseDTO(null, 500, "exception caused")
        }
    }
}