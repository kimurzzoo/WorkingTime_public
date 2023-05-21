package com.workingtime.chat.util.date

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

        fun secondToDatediff(secondTime : Int) : String
        {
            val hours = secondTime / 3600
            val minutes = (secondTime - hours * 3600) / 60
            val seconds = secondTime - hours * 3600 - minutes * 60

            return hours.toString() + "시간 " + minutes.toString() + "분 " + seconds.toString() + "초"
        }
    }
}