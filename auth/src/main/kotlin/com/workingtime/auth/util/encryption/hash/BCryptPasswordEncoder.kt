package com.workingtime.auth.util.encryption.hash

import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoder {

    fun encode(plainText : String) : String
    {
        return BCrypt.hashpw(plainText, BCrypt.gensalt())
    }

    fun matches(plainText : String, hashedText : String) : Boolean
    {
        return BCrypt.checkpw(plainText, hashedText)
    }
}