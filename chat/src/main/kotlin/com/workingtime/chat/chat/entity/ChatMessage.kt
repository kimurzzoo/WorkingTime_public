package com.workingtime.chat.chat.entity

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
    val userId : Long
)