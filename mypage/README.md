# WorkingTime - mypage

## Description

- WorkingTime 마이페이지 REST API 서버


## API Lists

- API 종류
  1. [info](#1-info)
  2. [changenickname](#2-change-nickname)
  3. [changecompany](#3-change-company)
  4. [mychecks](#4-my-checks)


### [1](#api-lists). Info

    GET /mypage/info

| message  |  info  |     field     |        type         |     description      |
| :------: | :----: | :-----------: | :-----------------: | :------------------: |
| request  | header | Content-Type  |  application/json   |      json 요청       |
|          | header | Authorization | {your access token} |     access token     |
| response | header | Content-Type  |  application/json   |      json 응답       |
|          |  data  |   nickname    |       string        |       nickname       |
|          |  data  |  companyName  |       string        |      회사 이름       |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

#


### [2](#api-lists). Change Nickname

    GET /mypage/changenickname

| message  |  info  |     field     |        type         |     description      |
| :------: | :----: | :-----------: | :-----------------: | :------------------: |
| request  | header | Content-Type  |  application/json   |      json 요청       |
|          | header | Authorization | {your access token} |     access token     |
|          | query  |  newnickname  |       string        |      새 닉네임       |
| response | header | Content-Type  |  application/json   |      json 응답       |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

#

### [3](#api-lists). Change Company

    GET /mypage/changecompany

| message  |  info  |     field     |        type         |     description      |
| :------: | :----: | :-----------: | :-----------------: | :------------------: |
| request  | header | Content-Type  |  application/json   |      json 요청       |
|          | header | Authorization | {your access token} |     access token     |
|          | query  |  companyname  |       string        |     새 회사 이름     |
| response | header | Content-Type  |  application/json   |      json 응답       |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

#

### [4](#api-lists). My Checks

    POST /mypage/mychecks

| message  |  info  |     field      |        type         |      description      |
| :------: | :----: | :------------: | :-----------------: | :-------------------: |
| request  | header |  Content-Type  |  application/json   |       json 요청       |
|          | header | Authorization  | {your access token} |     access token      |
|          |  data  |   startDate    |       string        |       시작 기간       |
|          |  data  |    endDate     |       string        |       종료 기간       |
|          |  data  |  companyName   |       string        |       회사 이름       |
|          |  data  | minWorkingHour |         int         |    최소 근로 시간     |
|          |  data  | maxWorkingHour |         int         |    최대 근로 시간     |
|          |  data  |    pageNum     |         int         |      페이지 번호      |
| response | header |  Content-Type  |  application/json   |       json 응답       |
|          |  data  |    maxPage     |         int         |   최대 페이지 갯수    |
|          |  data  |     checks     |        list         | 근로 시간 기록 리스트 |
|          |  data  |      code      |         int         | response status code  |
|          |  data  |  description   |       string        | response description  |

checks

|    field    |  type  |       description       |
| :---------: | :----: | :---------------------: |
|     id      |  long  |        check id         |
|  startTime  | string | start time of the check |
|   endTime   | string |  end time of the check  |
| workingTime | string |        근로 시간        |
| companyName | string |        회사 이름        |

#