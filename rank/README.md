# WorkingTime - rank

## Description

- WorkingTime 근로 시간 별 순위 REST API 서버


## API Lists

- API 종류
  1. [avgmine](#1-average-mine)
  2. [avgcompany](#2-average-company)
  3. [avgallcompany](#3-average-all-company)
  4. [avgrank](#4-average-rank)


### [1](#api-lists). Average Mine

    POST /rank/avgmine

| message  |  info  |     field     |        type         |      description       |
| :------: | :----: | :-----------: | :-----------------: | :--------------------: |
| request  | header | Content-Type  |  application/json   |       json 요청        |
|          | header | Authorization | {your access token} |      access token      |
|          |  data  |  companyName  |       string        |       회사 이름        |
|          |  data  |   startDate   |       string        |       시작 기간        |
|          |  data  |    endDate    |       string        |       종료 기간        |
| response | header | Content-Type  |  application/json   |       json 응답        |
|          |  data  |    avgTime    |       string        | 기간 내 근로 시간 총합 |
|          |  data  |     code      |         int         |  response status code  |
|          |  data  |  description  |       string        |  response description  |

#


### [2](#api-lists). Average Company

    GET /rank/avgcompany

| message  |  info  |     field     |        type         |      description       |
| :------: | :----: | :-----------: | :-----------------: | :--------------------: |
| request  | header | Content-Type  |  application/json   |       json 요청        |
|          | header | Authorization | {your access token} |      access token      |
|          | query  |   duration    |       string        |       기록 기간        |
| response | header | Content-Type  |  application/json   |       json 응답        |
|          |  data  |    avgTime    |       string        | 기간 내 근로 시간 총합 |
|          |  data  |     code      |         int         |  response status code  |
|          |  data  |  description  |       string        |  response description  |

#

### [3](#api-lists). Average All Company

    GET /rank/avgallcompany

| message  |  info  |     field     |        type         |      description       |
| :------: | :----: | :-----------: | :-----------------: | :--------------------: |
| request  | header | Content-Type  |  application/json   |       json 요청        |
|          | header | Authorization | {your access token} |      access token      |
|          | query  |   duration    |       string        |       기록 기간        |
| response | header | Content-Type  |  application/json   |       json 응답        |
|          |  data  |    avgTime    |       string        | 기간 내 근로 시간 총합 |
|          |  data  |     code      |         int         |  response status code  |
|          |  data  |  description  |       string        |  response description  |

#

### [4](#api-lists). Average Rank

    GET /rank/avgrank

| message  |  info  |     field     |        type         |     description      |
| :------: | :----: | :-----------: | :-----------------: | :------------------: |
| request  | header | Content-Type  |  application/json   |      json 요청       |
|          | header | Authorization | {your access token} |     access token     |
|          | query  |     range     |         int         |    5위/20위/100위    |
|          | query  |   duration    |       string        |      기록 기간       |
|          | query  |     order     |       string        |     짧은/긴 순서     |
| response | header | Content-Type  |  application/json   |      json 응답       |
|          |  data  |     ranks     |        list         |     순위 리스트      |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |

ranks

|    field    |  type  | description |
| :---------: | :----: | :---------: |
| companyName | string |  회사 이름  |
|   avgTime   | string |  근로 시간  |

#