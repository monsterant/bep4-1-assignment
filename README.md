# 실행 방법

## 개발 환경

- JDK: Java 25
- Spring Boot: 4.0.1
- Build Tool: Gradle
- Database: H2

## DB 설정

H2 File DB를 사용합니다.

```yaml
spring:
  datasource:
    url: jdbc:h2:./db_dev;MODE=MySQL
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: update

```

## 구조 설명

### 모듈구성 

1. member 모듈

- 회원 가입

- 글작성 시 활동 점수 +3점

- 댓글 작성시 활동 점수 +1 점

- member API V1 팁기능

2. post 모듈

- 글 작성

- 댓글 작성

- PostMember로 member 테이블 값을 복사

- 해당 PostMember를 통해서 member와의 결합도를 끊음

3. shard 모듈

- 공통 기능으로 member 복사 및 member 이벤트 정의

- member 모듈 복사 dto 제공

### 이벤트와 http api를 구분한 이유

현재 IN OUT DOMAIN APP로 분리된 구조

여기서 이벤트를 활용하여 모듈간에 결합도를 낮춥니다.

이 때 이벤트에 경우 통신간 응답이 불가능하기에 응답이 필요한 경우

API를 통해서 통신에 대한 응답을 보내고 있습니다.

### 회원 복제 흐름

1. post에서 eventListner가 이벤트 수신

2. @TransactionalEventListener(phase = AFTER_COMMIT) 통해서
   선행된 ransaction 진행

3. 이벤트에 따라서 postfacde 내에 syncMember 진행

4. syncMember 통해서 필요한 member 정보 취합 후 postMember로 저장

5. 실제 member에 영향을 주지 않고 postMember 복제본

## 확인결과

### 초기 데이터 갯수

실행 SQL

```sql
SELECT COUNT(*) FROM MEMBER_MEMBER;
SELECT COUNT(*) FROM POST_POST;
SELECT COUNT(*) FROM POST_POST_COMMENT;
```

확인 결과

```text
MEMBER_MEMBER = 6
POST_POST = 6
POST_POST_COMMENT = 8
```
### 회원별 활동점수

활동점수는 글 작성 시 3점, 댓글 작성 시 1점 증가합니다.

```sql
SELECT * FROM MEMBER_MEMBER 
```

```text
user1
- 글 3개: 3 × 3 = 9점
- 댓글 3개: 3 × 1 = 3점
- 총 활동점수: 12점

user2
- 글 2개: 2 × 3 = 6점
- 댓글 3개: 3 × 1 = 3점
- 총 활동점수: 9점

user3
- 글 1개: 1 × 3 = 3점
- 댓글 2개: 2 × 1 = 2점
- 총 활동점수: 5점
```

### 원본과 복제본 일치 확인
```sql
SELECT * FROM MEMBER_MEMBER ;

SELECT * FROM POST_MEMBER; 
```
sql을 통해서 원본과 복제 post 회원 정보 일치 확인

### 재실행 시 중복 없음

동일한 H2 DB를 유지한 상태에서 애플리케이션을 종료 후 재실행했습니다.

재실행 전

회원 6명
글 6개
댓글 8개

user1 = 12점
user2 = 9점
user3 = 5점

재실행 후

회원 6명
글 6개
댓글 8개

user1 = 12점
user2 = 9점
user3 = 5점

재실행 후에도 초기 데이터가 중복 생성되지 않고 활동점수도 추가 증가하지 않음을 확인했습니다.

### 비밀번호 팁.

http://localhost:8080/api/v1/member/members/randomSecureTip 실행시 

비밀번호의 유효기간은 90일 입니다. 출력 확인