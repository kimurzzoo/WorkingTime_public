package com.workingtime.auth.account.service

import com.workingtime.auth.account.dto.LoginDTO
import com.workingtime.auth.account.dto.TokenResultDTO
import com.workingtime.auth.account.dto.RegisterDTO
import com.workingtime.auth.account.dto.TokenDTO
import com.workingtime.auth.account.entity.RefreshToken
import com.workingtime.auth.account.entity.User
import com.workingtime.auth.account.jwt.JwtTokenProvider
import com.workingtime.auth.account.repository.UserRepository
import com.workingtime.auth.account.repository.redis.RefreshTokenRepository
import com.workingtime.auth.check.repository.CheckRepository
import com.workingtime.auth.util.encryption.hash.BCryptPasswordEncoder
import com.workingtime.auth.util.encryption.sym.AES256Encoder
import com.workingtime.auth.util.network.dto.ResponseDTO
import com.workingtime.auth.util.network.email.EmailService
import com.workingtime.auth.util.network.web.IpUtil
import com.workingtime.auth.util.redis.RedisUtil
import com.workingtime.auth.util.string.StringUtil.Companion.email_regex
import jakarta.servlet.http.HttpServletRequest
import org.hibernate.exception.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AuthService(private val userRepository : UserRepository,
                  private val checkRepository: CheckRepository,
                  private val emailService: EmailService,
                  private val jwtTokenProvider: JwtTokenProvider,
                  private val refreshTokenRepository: RefreshTokenRepository,
                  private val passwordEncoder: BCryptPasswordEncoder,
                  private val redisUtil : RedisUtil,
                  private val encoder : AES256Encoder) {

    private val logger = LoggerFactory.getLogger("AuthService")

    @Transactional(rollbackFor = [Exception::class])
    fun register(registerDTO : RegisterDTO, ipaddress : String) : ResponseDTO
    {
        logger.info("{} : register nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
        val result : ResponseDTO = ResponseDTO(200, "")
        try {
            if(registerDTO.password.length < 10 || registerDTO.password.length > 30)
            {
                result.code = 1001
                result.description = "The length of password is too short or too long"
                logger.info("{} : register length failed nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
                return result
            }

            if(registerDTO.password != registerDTO.passwordConfirm)
            {
                result.code = 1002
                result.description = "The password isn't confirmed"
                logger.info("{} : register confirm failed nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
                return result
            }

            if(registerDTO.nickname.length < 2 || registerDTO.nickname.length > 20)
            {
                result.code = 1003
                result.description = "nickname is too short or too long"
                logger.info("{} : register nickname failed nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
                return result
            }

            if(!registerDTO.email.matches(email_regex))
            {
                result.code = 1004
                result.description = "email address pattern is wrong"
                logger.info("{} : register email failed nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
                return result
            }

            if(userRepository.existsByEmailAddress(registerDTO.email))
            {

                result.code = 1005
                result.description = "email address already exists"
                logger.info("{} : register duplicate failed nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
                return result
            }

            val user : User = User(encoder.encrypt(registerDTO.nickname), encoder.encrypt(registerDTO.email), passwordEncoder.encode(registerDTO.password), encoder.encrypt("ROLE_USER"))
            userRepository.save(user)

            result.code = 200
            result.description = "register success"

            logger.info("{} : register success nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
            return result
        }
        catch (e : DataIntegrityViolationException)
        {
            e.printStackTrace()
            result.code = 1006
            result.description = "email address already exists"
            logger.info("{} : register duplicate failed nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
            return result
        }
        catch (e : ConstraintViolationException)
        {
            e.printStackTrace()
            result.code = 1007
            result.description = "email address already exists"
            logger.info("{} : register duplicate failed nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
            return result
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            result.code = 500
            result.description = "exception caused"
            logger.info("{} : register exception caused nickname : {}, email : {}, password : {}, passwordConfirm : {}", ipaddress, registerDTO.nickname, registerDTO.email, registerDTO.password, registerDTO.passwordConfirm)
            return result
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun login(loginDTO: LoginDTO, request: HttpServletRequest) : TokenResultDTO
    {
        val ipaddress = IpUtil.getRemoteIp(request)
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(loginDTO.email))
            if(user == null)
            {
                return TokenResultDTO(null, ResponseDTO(1000, "there is no user like you"))
            }
            else if(!passwordEncoder.matches(loginDTO.password, user.m_password))
            {
                return TokenResultDTO(null, ResponseDTO(1042, "wrong password"))
            }
            else if(!user.enabled)
            {
                return TokenResultDTO(null, ResponseDTO(1043, "you are banned"))
            }
            else
            {
                val userRealEmail = encoder.decrypt(user.emailAddress)
                val indicator : String = UUID.randomUUID().toString()
                val accessToken : String = jwtTokenProvider.createToken(userRealEmail, encoder.decrypt(user.role))
                val refreshToken : String = jwtTokenProvider.createRefreshToken(indicator)
                refreshTokenRepository.save(RefreshToken(refreshToken, indicator, userRealEmail))
                return TokenResultDTO(TokenDTO(accessToken, indicator, refreshToken), ResponseDTO(200, "login success"))
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return TokenResultDTO(null, ResponseDTO(500, "exception caused"))
        }

    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun sendVerificationEmail(email : String) : ResponseDTO
    {
        try {
            val user = userRepository.findByEmailAddress(encoder.encrypt(email))
            if (user != null) {
                if(!user.verified)
                {
                    emailService.sendSimpleMessage(email)
                    return ResponseDTO(200, "send verification email success")
                }
                else
                {
                    return ResponseDTO(1021, "already verified")
                }
            }
            else
            {
                return ResponseDTO(1000, "there is no user like you")
            }
        }
        catch (e : Exception)
        {
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = [Exception::class])
    fun emailVerification(email : String, redeemcode : String, ipaddress: String) : ResponseDTO
    {
        try {
            val user : User? = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return ResponseDTO(1000, "there is no user like you")
            }
            else
            {
                if(user.verified)
                {
                    return ResponseDTO(1012, "Already verified")
                }
                else {
                    val verifiedresult = emailService.verifyEmail(email, redeemcode)
                    if(verifiedresult.code == 200)
                    {
                        user.verified = true
                        user.role = encoder.encrypt("ROLE_VERIFIEDUSER")
                        userRepository.save(user)
                        return ResponseDTO(200, "email verification success")
                    }
                    else
                    {
                        return ResponseDTO(1013, "email verification failed")
                    }
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return TokenResultDTO(null, ResponseDTO(500, "exception caused"))
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun reissue(indicator : String, refreshtoken : String) : TokenResultDTO
    {
        try {
            println("reissue - refreshtoken : $refreshtoken, indicator : $indicator")
            val refreshTokenOp = refreshTokenRepository.findByIdAndIndicator(refreshtoken, indicator)
            if(refreshTokenOp == null)
            {
                return TokenResultDTO(null, ResponseDTO(1051, "refresh token is expired"))
            }
            else
            {
                val user : User? = userRepository.findByEmailAddress(encoder.encrypt(refreshTokenOp.email))
                if(user == null)
                {
                    return TokenResultDTO(null, ResponseDTO(1000, "there is no one like you"))
                }
                else
                {
                    val accessToken : String = jwtTokenProvider.createToken(refreshTokenOp.email, encoder.decrypt(user.role))
                    val refreshToken : String = jwtTokenProvider.createRefreshToken(indicator)
                    refreshTokenRepository.deleteById(refreshtoken)
                    refreshTokenRepository.save(RefreshToken(refreshToken, indicator, encoder.decrypt(user.emailAddress)))
                    return TokenResultDTO(TokenDTO(accessToken, indicator, refreshToken), ResponseDTO(200, "reissue success"))
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return TokenResultDTO(null, ResponseDTO(500, "exception caused"))
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun logout(email: String, indicator: String, refreshtoken: String) : ResponseDTO
    {
        try {
            val refreshToken = refreshTokenRepository.findByIdAndIndicator(refreshtoken, indicator)
            if(refreshToken != null)
            {
                if(email == refreshToken.email)
                {
                    refreshTokenRepository.deleteById(refreshToken.id)
                    return ResponseDTO(200, "logout success")
                }
                else
                {
                    return ResponseDTO(1062, "the owner of your access token and the owner of your refresh token is different")
                }
            }
            else
                return ResponseDTO(1061, "already logout")
        } catch (e : Exception) {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun withdrawal(email : String) : ResponseDTO
    {
        return try {
            val userEncodedEmail = encoder.encrypt(email)
            if(userRepository.existsByEmailAddress(userEncodedEmail)) {
                val user = userRepository.findByEmailAddress(userEncodedEmail)
                checkRepository.deleteByUserId(user!!.id!!)
                userRepository.deleteByEmailAddress(userEncodedEmail)
                redisUtil.deleteData(email)
                ResponseDTO(200, "withdrawal success")
            } else {
                ResponseDTO(1000, "you are not registered")
            }
        } catch (e : Exception) {
            e.printStackTrace()
            ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun changePassword(email : String, password : String, newPassword : String, newPasswordConfirm : String) : ResponseDTO
    {
        try {
            val user = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return ResponseDTO(1000, "there is no user like you")
            }
            else if(!passwordEncoder.matches(password, user.m_password))
            {
                return ResponseDTO(1081, "password doesn't match")
            }
            else if(newPassword.length < 10)
            {
                return ResponseDTO(1082, "password is too short")
            }
            else if(newPassword != newPasswordConfirm)
            {
                return ResponseDTO(1083, "new password isn't confirmed")
            }
            else
            {
                user.m_password = passwordEncoder.encode(newPassword)
                userRepository.save(user)
                return ResponseDTO(200, "change password success")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun resetPassword(email : String) : ResponseDTO
    {
        try
        {
            val user = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return ResponseDTO(1000, "there is no user like you")
            }
            else
            {
                val newPassword = emailService.sendPasswordMessage(email)
                user.m_password = passwordEncoder.encode(newPassword)
                userRepository.save(user)
                return ResponseDTO(200, "reset password success")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ResponseDTO(500, "exception caused")
        }
    }
}