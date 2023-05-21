package com.workingtime.auth.util.network.cookie

import com.workingtime.auth.account.jwt.JwtTokenProvider.Companion.refreshTokenValidTime
import com.workingtime.auth.account.jwt.JwtTokenProvider.Companion.tokenValidTime
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class CookieProvider {

    fun createAccessTokenCookie(accessToken : String) : ResponseCookie
    {
        return ResponseCookie.from("Authorization", URLEncoder.encode("Bearer $accessToken", "UTF-8"))
            .httpOnly(false)
            .secure(true)
            //.secure(false)
            .path("/")
            .sameSite("None")
            .maxAge(tokenValidTime / 1000).build()
    }

    fun createRefreshTokenCookie(refreshToken : String) : ResponseCookie
    {
        return ResponseCookie.from("refreshtoken", URLEncoder.encode(refreshToken, "UTF-8"))
            .httpOnly(true)
            .secure(true)
            //.secure(false)
            .path("/")
            .sameSite("None")
            .maxAge(refreshTokenValidTime / 1000).build()
    }

    fun removeCookie(cookiename : String, path : String, secure : Boolean, httpOnly : Boolean) : Cookie?
    {
        val cookie = Cookie(cookiename, null)
        cookie.path = path
        cookie.secure = secure
        cookie.isHttpOnly = httpOnly
        cookie.maxAge = 0
        return cookie
    }


    fun of(responseCookie: ResponseCookie): Cookie? {
        val cookie = Cookie(responseCookie.name, responseCookie.value)
        cookie.path = responseCookie.path
        cookie.secure = responseCookie.isSecure
        cookie.isHttpOnly = responseCookie.isHttpOnly
        cookie.maxAge = responseCookie.maxAge.seconds.toInt()
        cookie.setAttribute("SameSite", responseCookie.sameSite)
        return cookie
    }

    fun addTokenCookies(response : HttpServletResponse, accessToken : String, indicator : String, refreshToken : String)
    {
        val accessTokenCookie: ResponseCookie = createAccessTokenCookie(accessToken)
        val refreshTokenCookie: ResponseCookie = createRefreshTokenCookie(refreshToken)

        val accessCookie: Cookie? = of(accessTokenCookie)
        val refreshCookie: Cookie? = of(refreshTokenCookie)
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)
    }

    fun deleteTokenCookies(response : HttpServletResponse)
    {
        val accessCookie: Cookie? = removeCookie("Authorization", "/", true, false)
        val refreshCookie: Cookie? = removeCookie("refreshtoken", "/", true, true)
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)
    }
}