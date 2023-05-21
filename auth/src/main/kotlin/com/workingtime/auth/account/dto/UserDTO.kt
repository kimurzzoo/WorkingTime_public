package com.workingtime.auth.account.dto

import com.workingtime.auth.util.network.dto.ResponseDTO

data class TokenDTO(
    val accessToken : String,
    val indicator : String,
    val refreshToken : String
)

data class RegisterDTO(
    val nickname : String,
    val email : String,
    val password : String,
    val passwordConfirm : String
)

class LoginDTO(email : String, password: String)
{
    var email : String = email
    var password : String = password

    constructor() : this("", "")
    {

    }
}

class TokenResultDTO(val tokens : TokenDTO?, private val responseDTO: ResponseDTO) : ResponseDTO(responseDTO.code, responseDTO.description)
{
}

data class ChangePasswordDTO(
    val password : String,
    val newPassword : String,
    val newPasswordConfirm : String
)