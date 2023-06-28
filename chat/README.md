# WorkingTime - chat

## Description

- WorkingTime 채팅 서버


## API Lists

- API 종류
    1. [ChatRoom](#1-chatroom)
    2. [Chatting Endpoint](#2-chatting-endpoint)
    3. [Sending Message](#3-sending-message)
    4. [Receiving Message](#4-receiving-message)


### [1](#api-lists). Chatroom

    GET /chatroom

| message  |  info  |     field     |        type         |     description      |
|:--------:|:------:|:-------------:|:-------------------:|:--------------------:|
| request  | header | Content-Type  |  application/json   |       json 요청        |
|          | header | Authorization | {your access token} |     access token     |
| response | header | Content-Type  |  application/json   |       json 응답        |
|          |  data  |   chatroom    |      ChatRoom       |      현재 채팅방 정보       |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

chatroom

|  field   |  type  |    description    |
|:--------:|:------:|:-----------------:|
|  roomId  |  long  |  room id (회사 id)  |
| roomName | string | room name (회사 이름) |
|  userId  |  long  |      user id      |


#


### [2](#api-lists). Chatting Endpoint

    /chatting

STOMP Endpoint

#


### [3](#api-lists). Sending Message

    /pub/message/{roomId}

STOMP SEND를 사용해 roomId로 지정된 room에 메시지를 전송한다.

#

### [4](#api-lists). Receiving Message

    /sub/message/{roomId}

STOMP MESSAGE를 사용해 roomId로 지정된 room에서 메시지를 받는다.

#