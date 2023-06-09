# WorkingTime - auth

## Description

- WorkingTime 인증/인가 REST API 서버


## API Lists

- API 종류
  1. [login](#1-login)
  2. [register](#2-register)
  3. [sendverificationemail](#3-send-verification-email)
  4. [emailverification](#4-email-verification)
  5. [reissue](#5-reissue)
  6. [logout](#6-logout)
  7. [withdrawal](#7-withdrawal)


### [1](#api-lists). Login

    POST /login
    
| message  |  info  |     field     |          type           |     description      |
|:--------:|:------:|:-------------:|:-----------------------:|:--------------------:|
| request  | header | Content-Type  |    application/json     |       json 요청        |
|          |  data  |     email     |         string          |        이메일 주소        |
|          |  data  |   password    |         string          |         비밀번호         |
| response | header | Content-Type  |    application/json     |       json 응답        |
|          | cookie | Authorization |   {your access token}   |     access token     |
|          | cookie | refreshtoken  |  {your refresh token}   |    refresh token     |
|          |  data  |     code      |           int           | response status code |
|          |  data  |  description  |         string          | response description |


#


### [2](#api-lists). Register

    POST /auth/register

| message  |  info  |      field      |          type           |     description      |
|:--------:|:------:|:---------------:|:-----------------------:|:--------------------:|
| request  | header |  Content-Type   |    application/json     |       json 요청        |
|          |  data  |    nickname     |         string          |         닉네임          |
|          |  data  |      email      |         string          |        이메일 주소        |
|          |  data  |    password     |         string          |         비밀번호         |
|          |  data  | passwordConfirm |         string          |       비밀번호 확인        |
| response | header |  Content-Type   |    application/json     |       json 응답        |
|          | cookie |  Authorization  |   {your access token}   |     access token     |
|          | cookie |  refreshtoken   |  {your refresh token}   |    refresh token     |
|          |  data  |      code       |           int           | response status code |
|          |  data  |   description   |         string          | response description |


#

### [3](#api-lists). Send Verification Email

    GET /auth/sendverificationemail

| message  |  info  |     field      |          type           |     description      |
|:--------:|:------:|:--------------:|:-----------------------:|:--------------------:|
| request  | header |  Content-Type  |    application/json     |       json 요청        |
|          | header | Authorization  |   {your access token}   |     access token     |
| response | header |  Content-Type  |    application/json     |       json 응답        |
|          |  data  |      code      |           int           | response status code |
|          |  data  |  description   |         string          | response description |


#

### [4](#api-lists). Email Verification

    GET /auth/emailverification

| message  |  info  |     field     |        type         |     description      |
|:--------:|:------:|:-------------:|:-------------------:|:--------------------:|
| request  | header | Content-Type  |  application/json   |       json 요청        |
|          | header | Authorization | {your access token} |     access token     |
|          | query  |  redeemcode   |       string        |     redeem code      |
| response | header | Content-Type  |  application/json   |       json 응답        |
|          |  data  |     code      |         int         | response status code |
|          |  data  |  description  |       string        | response description |


#

### [5](#api-lists). Reissue

    GET /auth/reissue

| message  |  info  |     field      |          type           |     description      |
|:--------:|:------:|:--------------:|:-----------------------:|:--------------------:|
| request  | header |  Content-Type  |    application/json     |       json 요청        |
|          | cookie |  refreshtoken  |  {your refresh token}   |    refresh token     |
| response | header |  Content-Type  |    application/json     |       json 응답        |
|          | cookie | Authorization  |   {your access token}   |     access token     |
|          | cookie |  refreshtoken  |  {your refresh token}   |    refresh token     |
|          |  data  |      code      |           int           | response status code |
|          |  data  |  description   |         string          | response description |

#

### [6](#api-lists). Logout

    GET /auth/logout

| message  |  info  |     field      |          type           |          description          |
|:--------:|:------:|:--------------:|:-----------------------:|:-----------------------------:|
| request  | header |  Content-Type  |    application/json     |            json 요청            |
|          | header | Authorization  |   {your access token}   |         access token          |
|          | cookie |  refreshtoken  |  {your refresh token}   |         refresh token         |
| response | header |  Content-Type  |    application/json     |            json 응답            |
|          | cookie | Authorization  |          null           |     access token deletion     |
|          | cookie |  refreshtoken  |          null           |    refresh token deletion     |
|          |  data  |      code      |           int           |     response status code      |
|          |  data  |  description   |         string          |     response description      |

## 7. Withdrawal

    GET /auth/withdrawal

| message  |  info  |     field      |          type           |          description          |
|:--------:|:------:|:--------------:|:-----------------------:|:-----------------------------:|
| request  | header |  Content-Type  |    application/json     |            json 요청            |
|          | header | Authorization  |   {your access token}   |         access token          |
| response | header |  Content-Type  |    application/json     |            json 응답            |
|          | cookie | Authorization  |          null           |     access token deletion     |
|          | cookie |  refreshtoken  |          null           |    refresh token deletion     |
|          |  data  |      code      |           int           |     response status code      |
|          |  data  |  description   |         string          |     response description      |

#