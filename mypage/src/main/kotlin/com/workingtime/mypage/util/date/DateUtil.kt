package com.workingtime.mypage.util.date

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateUtil {



    companion object{

        val dateFormatStr = "yyyy-MM-dd HH:mm:ss"
        val dateFormat = DateTimeFormatter.ofPattern(dateFormatStr)

        fun dateToString(time : LocalDateTime?) : String?
        {
            if(time == null)
            {
                return null
            }
            return time.format(dateFormat)
        }

        fun stringToDate(timestr : String) : LocalDateTime
        {
            return LocalDateTime.parse(timestr, dateFormat)
        }
    }
}