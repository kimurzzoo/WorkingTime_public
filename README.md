# WorkingTime_public

Link : [WorkingTime][workingtimelink]

[workingtimelink]: https://workingtime.kro.kr

public repository of WorkingTime

#


## Description


- 근로 시간 기록 사이트
- 근로 시간을 기록하고 다른 회사와 비교 가능
- 회사 채팅 기능


#

## Structure

- 전체 구조
![structure](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/3686277b-6cb6-46f1-abf7-4e1b4e98ac86)

- CI/CD
  - Google Cloud Build (Docker)


## Web Pages

- 로그인 페이지
![login](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/a20f4a26-8823-4b07-806f-b0b41da343da)

- 회원가입 페이지
![register](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/8022b863-45b8-42a5-8600-8dcaae78723d)

- 이메일 인증 페이지
![emailverification_send](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/9807def7-3f1f-4b82-add6-ae4c76c8cda5)
![emailverification_submit](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/8cf55b63-3c42-4553-bbbc-fc6aa4de2206)

- 근로 시간 기록 페이지
![check_start](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/8083b53a-c81c-42b9-81a8-c68f37dc3e3a)
![check_do](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/6a2584b6-aeab-4398-81a5-424168909044)

- 마이페이지
![mypage](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/3b351832-ff68-4857-ab23-74d073fa6e86)

- 근로 기록 순위 페이지
![rank](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/e361b24b-ce56-43d3-b60e-195bb3d99a14)

- 채팅 페이지
![chat](https://github.com/kimurzzoo/WorkingTime_public/assets/29720824/1dc0473c-a97e-4181-bf6a-18b3e1055e41)


#


## Microservices

1. [Auth](https://github.com/kimurzzoo/WorkingTime_public/blob/main/auth/README.md)

2. [Check](https://github.com/kimurzzoo/WorkingTime_public/blob/main/check/README.md)

3. [MyPage](https://github.com/kimurzzoo/WorkingTime_public/blob/main/mypage/README.md)

4. [Rank](https://github.com/kimurzzoo/WorkingTime_public/blob/main/rank/README.md)

5. [Chat](https://github.com/kimurzzoo/WorkingTime_public/blob/main/chat/README.md)
