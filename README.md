# simplePay
simple pay

## 기능
- 결제
  - 카드번호, 유효기간, cvc, 할부기간, 결제금액, 부가세 정보로 결제 처리
- 취소
  - uuid, 취소금액, 부가세 정보로 결제 건에 대한 취소 처리
- 조회
  - uuid 정보로 결제/취소 건 조회

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
- controller 는 paymentController 하나에서 같이 수행
- 호출에 사용되는 값들이 url 에 노출되지 않게 post 사용
- @Valid 사용으로 입력값 검증
- 각 기능을 도메인으로 나뉘어 서비스 레이어를 총 3개로 나누어 개발
- 공통으로 사용되는 util 들은 utils 안에 작성
- 문구나 상수 값은 StaticValues 에 static 변수로 관리
- 중복된 코드를 제거하기 위해 코드 리팩토링 수행
- api 수행에 대한 에러 원인을 기술하기 위해 응답 dto 안에 errorMessage 기술
- 멀티스레드 환경에 대비하기 위해 concurrentHashMap 사용
  1. 결제요청에 대해서 같은 카드번호로 결제가 수행 중인지 확인 하기 위해 paymentCardInfo 캐시를 두어 결제중 확인
  2. 취소요청에 대해서 같은 원 결제에 대해 취소가 수행 중인지 확인 하기 위해 cancelPaymentInfo 캐시를 두어 취소중 확인
  
    
## 빌드 및 실행 방법
- spring boot application 실행 (./gradlew bootrun)
- postman 을 통해서 기본 기능 테스트
- 문제에 제공되는 3가지 테스트 케이스에 대한 test 코드는 paymentControllerTest.java 파일에 기술 되어있음 (./gradlew test)

