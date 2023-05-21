package com.workingtime.rank.rank.dao

interface AvgUserWorkingTimeDAO {
    fun getAvgtime() : Int?
}

interface AvgRankWorkingTimeDAO {
    fun getAvgtime() : Int?
    fun getId() : Long
    fun getName() : String
}