# WorkingTime - check

## Description

- WorkingTime 근로시간 체크 REST API 서버


## API Lists

- API 종류
  1. [startcheck](#1-start-check)
  2. [endcheck](#2-end-check)
  3. [modifystarttime](#3-modify-start-time)
  4. [modifyendtime](#4-modify-end-time)
  5. [deletecheck](#5-delete-check)
  6. [nowcheck](#6-now-check)


### [1](#api-lists). Start Check

    GET /check/startcheck

| message  |  info  |     field     |        type         |     description      |
| :------: | :----: | :-----------: | :-----------------: | :------------------: |
| request  | header | Content-Type  |  application/json   |      json 요청       |
|          | header | Authorization | {your access token} |     access token     |
| response | header | Content-Type  |  application/json   |      json 응답       |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

#


### [2](#api-lists). End Check

    GET /check/endcheck

| message  |  info  |     field     |        type         |     description      |
| :------: | :----: | :-----------: | :-----------------: | :------------------: |
| request  | header | Content-Type  |  application/json   |      json 요청       |
|          | header | Authorization | {your access token} |     access token     |
| response | header | Content-Type  |  application/json   |      json 응답       |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

#

### [3](#api-lists). Modify Start Time

    GET /check/modifystarttime

| message  |  info  |     field     |        type         |     description      |
| :------: | :----: | :-----------: | :-----------------: | :------------------: |
| request  | header | Content-Type  |  application/json   |      json 요청       |
|          | header | Authorization | {your access token} |     access token     |
|          | query  |    checkid    |        long         |       check id       |
|          | query  | modifiedtime  |       string        | start time to modify |
| response | header | Content-Type  |  application/json   |      json 응답       |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

#

### [4](#api-lists). Modify End Time

    GET /check/modifyendtime

| message  |  info  |     field     |        type         |     description      |
| :------: | :----: | :-----------: | :-----------------: | :------------------: |
| request  | header | Content-Type  |  application/json   |      json 요청       |
|          | header | Authorization | {your access token} |     access token     |
|          | query  |    checkid    |        long         |       check id       |
|          | query  | modifiedtime  |       string        | start time to modify |
| response | header | Content-Type  |  application/json   |      json 응답       |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

#

### [5](#api-lists). Delete Check

    GET /check/deletecheck

| message  |  info  |     field     |        type         |     description      |
| :------: | :----: | :-----------: | :-----------------: | :------------------: |
| request  | header | Content-Type  |  application/json   |      json 요청       |
|          | header | Authorization | {your access token} |     access token     |
|          | query  |    checkid    |        long         |       check id       |
| response | header | Content-Type  |  application/json   |      json 응답       |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

#

### [6](#api-lists). Now Check

    GET /check/nowcheck

| message  |  info  |     field     |        type         |       description        |
| :------: | :----: | :-----------: | :-----------------: | :----------------------: |
| request  | header | Content-Type  |  application/json   |        json 요청         |
|          | header | Authorization | {your access token} |       access token       |
| response | header | Content-Type  |  application/json   |        json 응답         |
|          |  data  |   nowCheck    |        json         | recent check data object |
|          |  data  |     code      |         int         |   response status code   |
|          |  data  |  description  |       string        |   response description   |


nowCheck

|   field   |  type  |       description       |
| :-------: | :----: | :---------------------: |
|  checkId  |  long  |        check id         |
| startTime | string | start time of the check |
|  endTime  | string |  end time of the check  |

#