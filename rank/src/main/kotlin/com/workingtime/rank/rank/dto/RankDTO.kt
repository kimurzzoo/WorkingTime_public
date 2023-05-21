package com.workingtime.rank.rank.dto

import com.workingtime.rank.util.network.dto.ResponseDTO

data class AvgMineDTO(
    val companyName: String?,
    val startDate : String?,
    val endDate : String?
)

class AvgUserWorkingTimeDTO(
    val avgTime : String?,
    code : Int,
    description : String
) : ResponseDTO(code = code, description = description)

data class RankWorkingTimeDTO(
    val companyName : String,
    val avgTime : String
)

class TopRankWorkingTimeDTO(
    val ranks : List<RankWorkingTimeDTO>,
    code : Int,
    description : String
) : ResponseDTO(code = code, description = description)