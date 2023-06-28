package com.workingtime.chat.chat.entity

import com.workingtime.chat.util.network.dto.ResponseDTO

class ChatMessage(
    val message : String
)

class ChatMessageToPubSub(
    val roomId : Long,
    val userId : Long,
    val message : String
)

class ChatMessageToClient(
    val userId : Long,
    val message : String
)

data class ChatRoom(
    val roomId : Long,
    val roomName : String,
    val userId : Long
)

class ChatRoomDTO(
    val chatroom : ChatRoom?,
    code : Int,
    description : String
) : ResponseDTO(code = code, description = description)