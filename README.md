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


