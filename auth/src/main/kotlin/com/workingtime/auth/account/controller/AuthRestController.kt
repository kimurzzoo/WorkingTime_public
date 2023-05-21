package com.workingtime.auth.account.controller

import com.workingtime.auth.account.dto.ChangePasswordDTO
import com.workingtime.auth.account.dto.LoginDTO
import com.workingtime.auth.account.dto.RegisterDTO
import com.workingtime.auth.account.dto.TokenResultDTO
import com.workingtime.auth.account.entity.RefreshToken
import com.workingtime.auth.account.jwt.JwtTokenProvider
import com.workingtime.auth.account.repository.redis.RefreshTokenRepository
import com.workingtime.auth.account.service.AuthService
import com.workingtime.auth.util.network.cookie.CookieProvider
import com.workingtime.auth.util.network.web.IpUtil
import com.workingtime.auth.util.network.dto.ResponseDTO
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class AuthRestController(private val authService: AuthService,
                         private val jwtTokenProvider: JwtTokenProvider,
                         private val cookieProvider: CookieProvider,
                         private val refreshTokenRepository: RefreshTokenRepository) {

    @PostMapping("/login")
    fun login(@RequestHeader headers : HttpHeaders, @RequestBody loginDTO: LoginDTO, request: HttpServletRequest, response: HttpServletResponse) : ResponseDTO
    {
        if(headers.containsKey("X-Authorization-Id"))
        {
            if(headers["X-Authorization-Id"]!!.isNotEmpty())
            {
                return ResponseDTO(1044, "you already login in")
            }
        }

        val loginResultDTO : TokenResultDTO = authService.login(loginDTO, request)
        if(loginResultDTO.code == 200 && loginResultDTO.tokens != null)
        {
            cookieProvider.addTokenCookies(response, loginResultDTO.tokens.accessToken, loginResultDTO.tokens.indicator, loginResultDTO.tokens.refreshToken)
        }
        return ResponseDTO(loginResultDTO.code, loginResultDTO.description)
    }

    @PostMapping("/register")
    fun register(@RequestHeader headers : HttpHeaders, @RequestBody registerDTO : RegisterDTO, request: HttpServletRequest, response: HttpServletResponse) : ResponseDTO
    {
        if(headers.containsKey("X-Authorization-Id"))
        {
            if(headers["X-Authorization-Id"]!!.isNotEmpty())
                return ResponseDTO(1044, "you already login in")
        }

        val responseDTO : ResponseDTO = authService.register(registerDTO, IpUtil.getRemoteIp(request))
        if(responseDTO.code == 200)
        {
            val indicator : String = UUID.randomUUID().toString()
            val accessToken : String = jwtTokenProvider.createToken(registerDTO.email, "ROLE_USER")
            val refreshToken : String = jwtTokenProvider.createRefreshToken(indicator)

            cookieProvider.addTokenCookies(response, accessToken, indicator, refreshToken)
            refreshTokenRepository.save(RefreshToken(registerDTO.email, indicator, refreshToken))
        }
        return ResponseDTO(responseDTO.code, responseDTO.description)
    }

    @GetMapping("/sendverificationemail")
    fun sendVerificationEmail(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String) : ResponseDTO
    {
        if (!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }
        return authService.sendVerificationEmail(email)
    }

    @GetMapping("/emailverification")
    fun emailVerification(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestParam(value = "redeemcode", defaultValue = "") redeemcode : String, request: HttpServletRequest, response : HttpServletResponse) : ResponseDTO
    {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(redeemcode))
        {
            return ResponseDTO(400, "Bad Request")
        }
        val emailVerificationResponse = authService.emailVerification(email, redeemcode, IpUtil.getRemoteIp(request))
        if(emailVerificationResponse.code == 200 || emailVerificationResponse.code == 1000)
        {
            cookieProvider.deleteTokenCookies(response)
        }
        return emailVerificationResponse
    }

    @GetMapping("/reissue")
    fun reissue(@RequestHeader(value = "indicator", defaultValue = "") indicator : String, @RequestHeader(value = "X-Refreshtoken", defaultValue = "") refreshtoken : String, request: HttpServletRequest, response: HttpServletResponse) : ResponseDTO
    {
        if (!StringUtils.hasText(indicator) || !StringUtils.hasText(refreshtoken))
        {
            return ResponseDTO(400, "Bad Request")
        }
        val reissueResultDTO : TokenResultDTO = authService.reissue(indicator, refreshtoken)
        if(reissueResultDTO.tokens != null)
        {
            cookieProvider.addTokenCookies(response, reissueResultDTO.tokens.accessToken, reissueResultDTO.tokens.indicator, reissueResultDTO.tokens.refreshToken)
        }
        return ResponseDTO(reissueResultDTO.code, reissueResultDTO.description)
    }

    @GetMapping("/logout")
    fun logout(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email: String, @RequestHeader(value = "indicator", defaultValue = "") indicator : String, @RequestHeader(value = "X-Refreshtoken", defaultValue = "") refreshtoken : String, request: HttpServletRequest, response: HttpServletResponse) : ResponseDTO
    {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(indicator))
        {
            return ResponseDTO(400, "Bad Request")
        }
        val logoutResponse = authService.logout(email, indicator, refreshtoken)
        if(logoutResponse.code == 200 || logoutResponse.code == 1000)
        {
            cookieProvider.deleteTokenCookies(response)
        }
        return logoutResponse
    }

    @GetMapping("/withdrawal")
    fun withdrawal(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, request: HttpServletRequest, response: HttpServletResponse) : ResponseDTO
    {
        if (!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }
        val withdrawalResponse = authService.withdrawal(email)
        if(withdrawalResponse.code == 200 || withdrawalResponse.code == 1000)
        {
            cookieProvider.deleteTokenCookies(response)
        }
        return withdrawalResponse
    }

    @PostMapping("/changepassword")
    fun changePassword(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, @RequestBody changePasswordDTO: ChangePasswordDTO, request: HttpServletRequest, response: HttpServletResponse) : ResponseDTO
    {
        if (!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }

        val changePasswordResponse = authService.changePassword(email, changePasswordDTO.password, changePasswordDTO.newPassword, changePasswordDTO.newPassword)
        if(changePasswordResponse.code == 200 || changePasswordResponse.code == 1000)
        {
            cookieProvider.deleteTokenCookies(response)
        }
        return changePasswordResponse
    }

    @GetMapping("/resetpassword")
    fun resetPassword(@RequestHeader(value = "X-Authorization-Id", defaultValue = "") email : String, request: HttpServletRequest, response: HttpServletResponse) : ResponseDTO
    {
        if (!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }

        val resetPasswordResponse = authService.resetPassword(email)
        if(resetPasswordResponse.code == 200 || resetPasswordResponse.code == 1000)
        {
            cookieProvider.deleteTokenCookies(response)
        }
        return resetPasswordResponse
    }

    @GetMapping("/forgotpassword")
    fun forgotPassword(@RequestParam(value = "email", defaultValue = "") email : String, request: HttpServletRequest, response: HttpServletResponse) : ResponseDTO
    {
        if (!StringUtils.hasText(email))
        {
            return ResponseDTO(400, "Bad Request")
        }

        val forgotPasswordResponse = authService.resetPassword(email)
        if(forgotPasswordResponse.code == 200 || forgotPasswordResponse.code == 1000)
        {
            cookieProvider.deleteTokenCookies(response)
        }
        return forgotPasswordResponse
    }
}