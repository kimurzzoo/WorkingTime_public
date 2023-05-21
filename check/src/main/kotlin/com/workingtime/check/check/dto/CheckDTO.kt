package com.workingtime.check.check.dto

import com.workingtime.check.util.network.dto.ResponseDTO

data class NowCheckDTO(
    val checkId : Long?,
    val startTime : String,
    val endTime : String?
)

class NowCheckResponseDTO(
    val nowCheck : NowCheckDTO?,
    code : Int,
    description : String
) : ResponseDTO(code = code, description = description)