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





### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 월 4억 5천만 명 이상 사용하는 트위터는 어떻게 만들어진 걸까요?

* `초당 600만 읽기, 6천 쓰기` 규모 극한의 대용량 트래픽 상황 속에서, 트위터는 어떻게 상시 `반응성`과 `가용성`을 보장할 수 있을까요?
* `1억 5천 명` 팔로워를 보유한 일론 머스크의 `실시간 트윗`을 어떻게 그 많은 팔로워들에게 `동시에 안정적으로 제공`할 수 있을까요?

이런 궁금증을 프로젝트를 직접 구현하며 해결해 보고자 하였습니다.

<br>
<br>





### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 주로 어떤 점들을 고민했나요?


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

등, 트위터 실제 서비스 상황을 가정하며 프로젝트를 진행하였습니다. 

<br>
<br>





### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 프로젝트 구성도

<img src="https://user-images.githubusercontent.com/71416000/268357715-b82cc893-47d9-4426-919a-165e4c9520ae.jpg">

<br>
<br>





<div><h3><img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 핵심 기능 API 시퀀스 다이어그램</h3></div>

<img src="https://user-images.githubusercontent.com/71416000/268354959-121ca480-172b-4c94-a05a-9177c878ebae.png">
<img src="https://user-images.githubusercontent.com/71416000/268354110-07d1aa4d-c8d4-400d-9afe-13937dd65cc7.png">

<br>
<br>





### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 주요 기술적 고민과 문제 해결 과정

* 읽기 및 쓰기 성능 개선 및 고가용성을 위한 Redis Cluster 구성하기([#81](https://github.com/f-lab-edu/twitter-clone/pull/81))

* Redis Pub/Sub을 활용한 비동기 메시징으로 다중 저장소 및 데이터 분산 처리 환경에서 데이터 정합성 보장하기([#73](https://github.com/f-lab-edu/twitter-clone/pull/73))

* Redis Pipeline을 활용한 일괄 처리로 성능 개선하기([#73](https://github.com/f-lab-edu/twitter-clone/pull/73))

* 보안을 위해 사용자 인증 세션 저장소의 빈 스코프를 요청 단위로 설정하기([#71](https://github.com/f-lab-edu/twitter-clone/pull/71))

* Redis Session으로 확장성 있는 사용자 인증 및 로그인 구현 환경 구축하기([#71](https://github.com/f-lab-edu/twitter-clone/pull/71))

* nGrinder를 활용한 부하 테스트로 성능 검증하기([#70](https://github.com/f-lab-edu/twitter-clone/pull/70))

* MySQL 복제를 위한 쿼리 요청 분기와 지연 처리 프록시 설정하기([#69](https://github.com/f-lab-edu/twitter-clone/pull/69))

* Redis 세션 저장소와 캐싱 저장소 분리로 성능 개선하기([#68](https://github.com/f-lab-edu/twitter-clone/pull/68))

* 트윗 쓰기 시점에 팔로워 개별 타임라인에 캐싱(fan-out)하여 읽기 집중 부하 문제 해결하기([#65](https://github.com/f-lab-edu/twitter-clone/pull/65))

* 스프링 IoC/DI 원리를 적용해 설정파일로 유연하게 기능 동작 방식 변경 및 제어 하기([#65](https://github.com/f-lab-edu/twitter-clone/pull/65))

* Spring의 ArgumentResolver를 활용해 세션 유지중 사용자 정보 자동획득 기능 구현하기([#62](https://github.com/f-lab-edu/twitter-clone/pull/62))

* 스프링 AOP로 로그인 여부를 확인하는 중복로직 제거하기([#61](https://github.com/f-lab-edu/twitter-clone/pull/61))

* 생성자 주입 방식과 setter를 제거한 불변성 객체 설계로 스레드 안전성 높이기([#56](https://github.com/f-lab-edu/twitter-clone/pull/56))

* 사용자의 계정 삭제요청을 soft delete 방식으로 처리해 저장소의 데이터 정합성 보호하기([#53](https://github.com/f-lab-edu/twitter-clone/pull/53))

* Spring REST Docs와 Spring HATEOAS를 활용해 변경에 독립적이고 협업에 용이한 API 문서 생성하기([#42](https://github.com/f-lab-edu/twitter-clone/pull/42))


<br>
<br>


### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> API 문서

<p align="center">
  <img src="https://user-images.githubusercontent.com/71416000/268426514-bc4ca03e-a472-42a8-a742-82b48e96cdaf.gif"/>
</p>

* `Spring REST Docs`와 `Spring HATEOAS`를 활용해 로이 필딩의 REST 요건을 충족하는, 변화에 독립적이며 자가충족하는 API 문서를 작성하였습니다(API 개발중 시점의 샘플입니다).
* [[클릭] API 문서 페이지 방문하기 ✨](https://gorgeous-mandazi-651201.netlify.app)

<br>
<br>





### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 스키마 ERD

<img src="https://user-images.githubusercontent.com/71416000/268024847-4ad2a1b6-e58d-47e6-9968-081226443b9a.png">
