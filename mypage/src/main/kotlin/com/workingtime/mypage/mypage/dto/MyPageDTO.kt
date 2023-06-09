package com.workingtime.mypage.mypage.dto

import com.workingtime.mypage.util.network.dto.ResponseDTO

data class MyChecksDTO(
    val startDate : String?,
    val endDate : String?,
    val companyName : String?,
    val minWorkingHour : Int?,
    val maxWorkingHour : Int?,
    val pageNum : Int
)

data class CheckDTO(
    val id : Long?,
    val startTime : String,
    val endTime : String?,
    val workingTime : String?,
    val companyName : String
)

class MyChecksResponseDTO(val maxPage : Int,
                            val checks : List<CheckDTO>,
                            code : Int,
                            description : String) : ResponseDTO(code = code, description = description) {

}

class InfoResponseDTO(val nickname : String, val companyName : String, code : Int, description: String) : ResponseDTO(code = code, description = description)