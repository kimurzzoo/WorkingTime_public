package com.workingtime.chat.chat.service

import com.google.gson.Gson
import com.workingtime.chat.account.repository.UserRepository
import com.workingtime.chat.chat.entity.ChatMessage
import com.workingtime.chat.chat.entity.ChatMessageToPubSub
import com.workingtime.chat.chat.entity.ChatRoom
import com.workingtime.chat.chat.pubsub.PubSubOutBoundGateway
import com.workingtime.chat.util.encryption.sym.AES256Encoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(private val userRepository: UserRepository,
                    private val messagingGateway: PubSubOutBoundGateway,
                    private val encoder : AES256Encoder) {

    private val gson = Gson()

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun sendMessage(email : String, message: ChatMessage, roomId : Long)
    {
        try
        {
            val user = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return
            }
            else if(!user.enabled)
            {
                return
            }
            else if(user.company == null)
            {
                return
            }
            else if(user.company!!.id != roomId)
            {
                return
            }
            else
            {
                val jsonString = gson.toJson(ChatMessageToPubSub(roomId, user.id!!, message.message))
                messagingGateway.sendToPubsub(jsonString)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun roomInfo(email : String) : ChatRoom?
    {
        try {
            val user = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return null
            }
            else if(!user.enabled)
            {
                return null
            }
            else if(user.company == null)
            {
                return null
            }
            else
            {
                return ChatRoom(user.company!!.id!!, user.id!!)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return null
        }
    }
}