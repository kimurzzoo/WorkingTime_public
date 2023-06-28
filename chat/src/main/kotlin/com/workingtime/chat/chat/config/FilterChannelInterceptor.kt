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
import org.springframework.stereotype.Component

@Component
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
        println("msg: " + headerAccessor.command.toString())
        println("nativeHeaders: " + headerAccessor.getHeader("nativeHeaders").toString())
        println("full headers : " + headerAccessor.messageHeaders)
        println("full message:$message")
        when(command)
        {
            CONNECT -> {
                val accessToken = headerAccessor.getFirstNativeHeader("Authorization")
                println("CONNECT - Authorization token : $accessToken")
                if(jwtAuthorizationUtil.validateToken(accessToken))
                {
                    println("CONNECT - Authorization token validate")
                    val email : String = jwtAuthorizationUtil.getUsername(accessToken!!)
                    println("CONNECT - email : $email")
                    val user = userRepository.findByEmailAddress(encoder.encrypt(email))
                    println("CONNECT - user id : " + user?.id)
                    if(user == null)
                    {
                        println("CONNECT - user null")
                        return null
                    }
                    else if(!user.enabled)
                    {
                        println("CONNECT - user banned")
                        return null
                    }
                }
                else
                {
                    println("CONNECT - user not validate")
                    return null
                }
            }
            SUBSCRIBE -> {
                val accessToken = headerAccessor.getFirstNativeHeader("Authorization")
                println("SUBSCRIBE - Authorization token : $accessToken")
                if(jwtAuthorizationUtil.validateToken(accessToken))
                {
                    println("SUBSCRIBE - validate success")
                    val email : String = jwtAuthorizationUtil.getUsername(accessToken!!)
                    println("SUBSCRIBE - email : $email")
                    val user = userRepository.findByEmailAddress(encoder.encrypt(email))
                    println("SUBSCRIBE - user id : " + user?.id)
                    if(user == null)
                    {
                        println("SUBSCRIBE - user null")
                        return null
                    }
                    else if(!user.enabled)
                    {
                        println("SUBSCRIBE - user banned")
                        return null
                    }
                    else
                    {
                        println("SUBSCRIBE - destination url : " + headerAccessor.getHeader("simpDestination") as String)
                        val roomid = (headerAccessor.getHeader("simpDestination") as String).replace("/sub/message/", "").toLong()
                        println("SUBSCRIBE - room id : $roomid")
                        if(roomid != user.company!!.id)
                        {
                            println("SUBSCRIBE - room id not matched")
                            return null
                        }
                    }
                }
                else
                {
                    println("SUBSCRIBE - validate failed")
                    return null
                }
            }
            SEND -> {
                val accessToken = headerAccessor.getFirstNativeHeader("Authorization")
                println("SEND - Authorization token : $accessToken")
                if(jwtAuthorizationUtil.validateToken(accessToken))
                {
                    println("SEND - validate success")
                    val email : String = jwtAuthorizationUtil.getUsername(accessToken!!)
                    println("SEND - email : $email")
                    val user = userRepository.findByEmailAddress(encoder.encrypt(email))
                    println("SEND - user id : " + user?.id)
                    if(user == null)
                    {
                        println("SEND - user null")
                        return null
                    }
                    else if(!user.enabled)
                    {
                        println("SEND - user banned")
                        return null
                    }
                    else
                    {
                        println("SEND - destination url : " + headerAccessor.getHeader("simpDestination") as String)
                        val roomid = (headerAccessor.getHeader("simpDestination") as String).replace("/pub/message/", "").toLong()
                        println("SEND - room id : $roomid")
                        if(roomid != user.company!!.id)
                        {
                            println("SEND - room id not matched")
                            return null
                        }
                    }
                }
                else
                {
                    println("SEND - validate failed")
                    return null
                }
            }
            else -> {

            }
        }
        return message
    }
}