package com.workingtime.chat.chat.service

import com.google.gson.Gson
import com.workingtime.chat.account.repository.UserRepository
import com.workingtime.chat.chat.entity.ChatMessage
import com.workingtime.chat.chat.entity.ChatMessageToPubSub
import com.workingtime.chat.chat.entity.ChatRoom
import com.workingtime.chat.chat.entity.ChatRoomDTO
import com.workingtime.chat.chat.pubsub.PubSubOutBoundGateway
import com.workingtime.chat.util.encryption.sym.AES256Encoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional


@Service
class ChatService(private val userRepository: UserRepository,
                    private val messagingGateway: PubSubOutBoundGateway,
                    private val encoder : AES256Encoder
) {

    @Autowired
    private lateinit var ac : ApplicationContext

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
                println("sendMessage : send message")
                val jsonString = Gson().toJson(ChatMessageToPubSub(roomId, user.id!!, message.message))
                messagingGateway.sendToPubsub(jsonString)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun roomInfo(email : String) : ChatRoomDTO
    {
        try {
            val user = userRepository.findByEmailAddress(encoder.encrypt(email))
            if(user == null)
            {
                return ChatRoomDTO(null, 1000, "there is no user like you")
            }
            else if(!user.enabled)
            {
                return ChatRoomDTO(null , 1001, "you are banned")
            }
            else if(user.company == null)
            {
                return ChatRoomDTO(null, 5001, "you are not belong to any company")
            }
            else
            {
                return ChatRoomDTO(ChatRoom(user.company!!.id!!, user.company!!.name, user.id!!), 200, "roomInfo success")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return ChatRoomDTO(null , 500, "exception caused")
        }
    }
}