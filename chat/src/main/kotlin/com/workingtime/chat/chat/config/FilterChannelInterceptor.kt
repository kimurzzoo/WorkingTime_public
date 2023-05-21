package com.workingtime.chat.chat.config

import com.google.gson.Gson
import com.workingtime.chat.account.repository.UserRepository
import com.workingtime.chat.jwt.JwtAuthorizationUtil
import com.workingtime.chat.util.encryption.sym.AES256Encoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand.*
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor

class FilterChannelInterceptor : ChannelInterceptor {

    @Autowired
    private lateinit var jwtAuthorizationUtil : JwtAuthorizationUtil

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var encoder: AES256Encoder

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val headerAccessor: StompHeaderAccessor = StompHeaderAccessor.wrap(message)
        val command = headerAccessor.command
        when(command)
        {
            CONNECT -> {
                val accessToken = headerAccessor.getFirstNativeHeader("Authorization")
                if(jwtAuthorizationUtil.validateToken(accessToken))
                {
                    val email : String = jwtAuthorizationUtil.getUsername(accessToken!!)
                    val user = userRepository.findByEmailAddress(encoder.encrypt(email))
                    if(user == null)
                    {
                        return null
                    }
                    else if(!user.enabled)
                    {
                        return null
                    }
                }
                else
                {
                    return null
                }
            }
            SUBSCRIBE, SEND -> {
                val accessToken = headerAccessor.getFirstNativeHeader("Authorization")
                if(jwtAuthorizationUtil.validateToken(accessToken))
                {
                    val email : String = jwtAuthorizationUtil.getUsername(accessToken!!)
                    val user = userRepository.findByEmailAddress(encoder.encrypt(email))
                    if(user == null)
                    {
                        return null
                    }
                    else if(!user.enabled)
                    {
                        return null
                    }
                    else
                    {
                        val roomid = (headerAccessor.getHeader("simpDestination") as String).replace("/sub/message/", "").toLong()
                        if(roomid != user.company!!.id)
                        {
                            return null
                        }
                    }
                }
                else
                {
                    return null
                }
            }
            else -> TODO()
        }
        return message
    }
}