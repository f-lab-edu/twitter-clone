<p align="center">
  <br>
  <br>
  <br>
  <div align="center"><img src="https://user-images.githubusercontent.com/71416000/266865230-90e2d7d4-56fe-4dff-b188-9b736beb6963.png" width="6%"/></div>
  <br>
  <div align="center"><img src="https://user-images.githubusercontent.com/71416000/267307351-7ba53886-9cc9-417e-9eff-14917aebe0b7.png" width="47%"/></div>
  <br>
</p>

<p align="center">"뉴스 속보와 엔터테인먼트부터 스포츠와 정치까지"<br> <span>트위터</span>를 모티브로 만든 소셜 네트워크 API 서버 토이 프로젝트입니다.</p>

<br>

<div align="right">
  <img src="https://img.shields.io/badge/Java-04B078?style=flat-square&logo=Java&logoColor=FFFFFF"/>
  <img src="https://img.shields.io/badge/Spring-04B078?style=flat-square&logo=Spring&logoColor=FFFFFF"/>
  <img src="https://img.shields.io/badge/SpringBoot-04B078?style=flat-square&logo=SpringBoot&logoColor=FFFFFF"/>
  <img src="https://img.shields.io/badge/Gradle-04B078?style=flat-square&logo=gradle&Color=FFFFFF"/>
  <img src="https://img.shields.io/badge/Mybatis-04B078?style=flat-square&logoColor=FFFFFF"/>
  <br>
  <img src="https://img.shields.io/badge/Mysql-04B078?style=flat-square&logo=MySql&logoColor=FFFFFF"/>
  <img src="https://img.shields.io/badge/Redis-04B078?style=flat-square&logo=Redis&logoColor=FFFFFF"/>
  <img src="https://img.shields.io/badge/Jenkins-04B078?style=flat-square&logo=Jenkins&logoColor=FFFFFF"/>
  <img src="https://img.shields.io/badge/Git-04B078?style=flat-square&logo=Git&logoColor=FFFFFF"/>
  <img src="https://img.shields.io/badge/GitHub-04B078?style=flat-square&logo=GitHub&logoColor=FFFFFF"/>
  <img src="https://img.shields.io/badge/NCP-04B078?style=flat-square&logo=Naver&logoColor=FFFFFF"/>
  <img src="https://img.shields.io/badge/intellij-04B078?style=flat-square&logo=intellijidea&Color=FFFFFF"/>
</div>

<br>
<br>
<br>
<br>





## <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 월 4억 5천만 명 이상 사용하는 트위터는 어떻게 만들어진 걸까요?

* `초당 450만 읽기, 6천 쓰기` 규모 극한의 대용량 트래픽 상황 속에서, 트위터는 어떻게 상시 `반응성`과 `가용성`을 보장할 수 있을까요?
* `1억 5천 명` 팔로워를 보유한 일론 머스크의 `실시간 트윗`을 어떻게 팔로워들에게 `동시에 지연 없이 제공`할 수 있을까요?

이런 궁금증을 프로젝트를 직접 구현하며 해결해 보고자 하였습니다.

<br>
<br>





## <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 주로 어떤 점들을 고민했나요?


<div align="center"><img src="https://user-images.githubusercontent.com/71416000/267387690-39471fd3-f7f9-4184-8ddd-e8500f6a0b81.png" width="50%"></div>

<br>

* `변경`과 `유지보수`가 용이한 `객체지향 디자인`은 어떻게 하는지
* 리소스 중심의 균일한 `REST API` 디자인은 어떻게 하는지
* `대용량 읽기 요청`에도 상시 빠른 `반응성(짧은 지연시간)`을 보장하는 방법
* 애플리케이션에 최적의 `저장소를 선택하는 기준`은 무엇들이 있는지
* 무중단 서비스를 위한 `가용성`을 보장하는 방법과 `트레이드 오프` 무엇인지
* `이종 저장소`와 `분산 처리 환경`에서 어떻게 `데이터 정합성`을 보장할 수 있는지
* 원활한 `협업`을 위한 `코딩 컨벤션`, `문서화`, `github(이슈, PR) 활용`은 어떻게 해야 하는지
* `API 테스트, 부하 테스트` 등 서비스 기능 검증을 위한 다양한 테스트를 어떻게 수행하는지

등, 트위터 실서비스 상황을 가정하며 프로젝트를 진행하였습니다. 

<br>
<br>





## <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 프로젝트 구성도

<img src="https://user-images.githubusercontent.com/71416000/268357715-b82cc893-47d9-4426-919a-165e4c9520ae.jpg">

<br>
<br>

















































## <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 주요 기술적 고민과 문제 해결 과정


1. 트윗 쓰기 시점에 팔로워 개별 타임라인에 캐싱(fan-out)하여 읽기 집중 부하 문제 해결하기 <img src="https://img.shields.io/badge/-DONE-red">

3. Redis Pipeline을 활용한 일괄 처리로 성능 개선하기 <img src="https://img.shields.io/badge/-DONE-red">

6. nGrinder를 활용한 부하 테스트로 성능 검증하기 <img src="https://img.shields.io/badge/-DONE-red">

15. 부하 테스트 성능 병목 지점 찾아 개선하기 <img src="https://img.shields.io/badge/-DONE-red">

1. 읽기 및 쓰기 성능 개선 및 고가용성을 위한 Redis Cluster 구성하기 <img src="https://img.shields.io/badge/-DONE-red">

2. MySQL 복제를 위한 쿼리 요청 분기와 지연 처리 프록시 설정하기 <img src="https://img.shields.io/badge/-DONE-red">

5. Redis Session으로 확장성 있는 사용자 인증 및 로그인 구현 환경 구축하기 <img src="https://img.shields.io/badge/-DONE-red">

1. Redis Pub/Sub을 활용한 비동기 메시징으로 분산 처리 다중 저장소 환경에서 데이터 정합성 보장하기 <img src="https://img.shields.io/badge/-DONE-red">

14. 사용자의 계정 삭제요청을 soft delete 방식으로 처리해 저장소의 데이터 정합성 보호하기 <img src="https://img.shields.io/badge/-DONE-red">
가
2. 빈 스코프를 요청 단위로 설정해 사용자 인증 세션 저장소를 안전하게 사용하기<img src="https://img.shields.io/badge/-DONE-red">

10. 스프링 IoC/DI 원리를 적용해 설정파일로 유연하게 기능 변경 및 제어 하기 <img src="https://img.shields.io/badge/-DONE-red">

11. 스프링의 ArgumentResolver를 활용해 세션 유지중 사용자 정보 자동획득 기능 구현하기 <img src="https://img.shields.io/badge/-DONE-red">

12. 스프링 AOP로 로그인 여부를 확인하는 중복로직 제거하기 <img src="https://img.shields.io/badge/-DONE-red">

13. 생성자 주입 방식과 setter를 제거한 불변성 객체 설계로 스레드 안전성 높이기 <img src="https://img.shields.io/badge/-DONE-red">

15. Spring REST Docs와 Spring HATEOAS를 활용해 변경에 독립적이고 협업에 용이한 API 문서 생성하기 <img src="https://img.shields.io/badge/-DONE-red">

15. 서버 배포을 위한 클라우드 아키텍쳐 구상 <img src="https://img.shields.io/badge/-DONE-red">

<br>















### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 기술적 고민, 문제해결 과정 아티클 보러가기(by Dongwook Song)

1. [대용량 읽기 쏠림을 견디는 SNS 알고리즘 구현하기(feat. pull vs. push)](https://velog.io/@rmndr/scalable-application-with-pull-push-for-low-latency) 👈 Click

2. [신뢰할 수 없는 여러 부품으로 신뢰할 수 있는 시스템 구축하기(feat. 분산 데이터))](https://velog.io/@rmndr/scalable-application-with-distributed-data) 👈 Click

3. (작성중)분산 데이터 환경에서 데이터의 정합성 보장하기

### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 기술적 고민, 문제해결 과정 아티클 보러가기(by Seungjun Lee)

1. [안정성과 확장성을 위한 클라우드 서버 세팅](https://velog.io/@sjlee0724/%EC%95%88%EC%A0%95%EC%84%B1%EA%B3%BC-%ED%99%95%EC%9E%A5%EC%84%B1%EC%9D%84-%EC%9C%84%ED%95%9C-%ED%81%B4%EB%9D%BC%EC%9A%B0%EB%93%9C-%EC%84%9C%EB%B2%84-%EC%84%B8%ED%8C%85) 👈 Click

3. [성능테스트 툴과 테스트 계획](https://velog.io/@sjlee0724/%EC%84%B1%EB%8A%A5%ED%85%8C%EC%8A%A4%ED%8A%B8-%EA%B3%84%ED%9A%8D-%EB%B0%8F-%EC%84%B8%ED%8C%85) 👈 Click

3. [성능테스트와 개선 1편 (쿼리와 캐싱)](https://velog.io/@sjlee0724/%EC%84%B1%EB%8A%A5%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%99%80-%EA%B0%9C%EC%84%A0-1%ED%8E%B8-%EC%BF%BC%EB%A6%AC%EC%99%80-%EC%BA%90%EC%8B%B1) 👈 Click

4. [성능테스트와 개선 2편 (로드 밸런싱과 DB 복제)](https://velog.io/@sjlee0724/%EC%84%B1%EB%8A%A5%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%99%80-%EA%B0%9C%EC%84%A0-2%ED%8E%B8-%EB%A1%9C%EB%93%9C-%EB%B0%B8%EB%9F%B0%EC%8B%B1%EA%B3%BC-DB-%EB%B3%B5%EC%A0%9C) 👈 Click


<br>
<br>


























<div><h2><img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 핵심 기능 API 시퀀스 다이어그램</h3></div>

<img src="https://user-images.githubusercontent.com/71416000/268354959-121ca480-172b-4c94-a05a-9177c878ebae.png">
<img src="https://user-images.githubusercontent.com/71416000/268354110-07d1aa4d-c8d4-400d-9afe-13937dd65cc7.png">

<br>
<br>






## <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> API 문서

<p align="center">
  <img src="https://user-images.githubusercontent.com/71416000/268426514-bc4ca03e-a472-42a8-a742-82b48e96cdaf.gif"/>
</p>

* `Spring REST Docs`를 활용해 API 테스트를 작성하고 API 문서를 자동 생성 하였으며, `Spring HATEOAS`를 통해 동적으로 URI를 생성해 응답 메시지에 포함시키도록 하였습니다. 응답 메시지에 클라이언트에게 필요한 모든 정보를 포함함으로써 클라이언트가 API 응답만으로 서버 변경사항을 해석하고 적용할 수 있도록 하고자 했습니다.
* [[클릭] API 문서 샘플 보러가기](https://voluble-semifreddo-9fff2a.netlify.app)  👈 Click 
    * (단, 링크의 문서는 최신버전의 API가 아니며, 예시를 보여드리기 위함입니다. 최신 버전 API의 문서화는 현재 진행중임을 알려드립니다.)




<br>
<br>





## <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 스키마


### MySQL 스키마 설계

<img src="https://user-images.githubusercontent.com/71416000/268024847-4ad2a1b6-e58d-47e6-9968-081226443b9a.png">

<br>

### Redis 캐싱 솔루션 키 설계

* 유저별 타임라인의 키: `{user_id}`
    * `user_id`는 스키마의 user 테이블의 `id` 필드값과 동일하며, 특정 유저를 식별하는 고유 UUID로 되어있습니다.
        * 키값 예: `550e8400-e29b-41d4-a716-336655330000`
    * 반환 값: 특정 유저의 타임라인(트윗 목록)을 반환합니다.
* 유저가 팔로우중인 셀럽유저 목록의 키: `celebs:{user_id}`
    * "`celebs:`"는 키의 앞에 붙는 미리 약속된 고정상수입니다.
    * `user_id`는 스키마의 user 테이블의 `id` 필드값과 동일하며, 특정 유저를 식별하는 고유 UUID로 되어있습니다.
        * 키값 예: `celebs:550e8400-e29b-41d4-a716-336655330000`
    * 반환 값: 특정 유저가 팔로우중인 셀럽유저 id 목록을 반환합니다.