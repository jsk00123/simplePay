# simplePay
simple pay

## 기능
- 결제요청
  - 카드번호, 유효기간, cvc, 할부기간, 결제금액, 부가세 정보로 결제 요청 처리
- 취소
  - 결제 건에 대한 취소 처리
- 조회
  - 결제 건 조회

## 기술
- 스프링부트
- 스프링 5
- DB : H2
- Java 8
- gradle
- JPA(Hibernate)
- handlebars
- git, github
- JUnit

## 테이블설계
- 카드 정보 저장 테이블
    - card_post_info 테이블 생성

columnName | 설명
-----------|------------------------
uuid       | 유니크 ID
originUuid | 취소 시에 원결제의 유니크 ID
postInfo   | 카드사에 전송하는 데이터
    
## 문제해결 전략
- 멀티스레드 환경에 대비하기 위해 concurrentHashMap 을 통해 중복요청 방지
    
## 빌드 및 실행 방법
- spring boot application 실행 (./gradlew bootrun)
- postman 을 통해서 기본 기능 테스트
- test 코드는 paymentControllerTest.java 파일에 기술 되어있음 (./gradlew test)

