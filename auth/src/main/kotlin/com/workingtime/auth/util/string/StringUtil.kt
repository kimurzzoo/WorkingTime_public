package com.workingtime.auth.util.string

class StringUtil {
    companion object
    {
        val email_regex = "[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}".toRegex()
    }
}