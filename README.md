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

* `변경`과 `유지보수`가 용이한 `객체지향적인 디자인`은 어떻게 적용하는지
* 확장성 있고 자원 기반의 균일한 `RESTful API`는 어떻게 설계되어야 하는지
* 성능을 위해 `DB 인덱스`를 어떻게 구성해야 하는지
* SQL, NoSQL 등 `최적의 저장소` 선택 기준에 어떤 것이 있는지
* 어떻게 `가용성`을 보장할 수 있고 `트레이드 오프`는 무엇인지
* `분산 트랜잭션의 한계`는 무엇이고 어떻게 신뢰성 있는 애플리케이션을 구현할 수 있을지
* `API 테스트, 부하 테스트` 등 서비스 기능 검증을 위한 다양한 테스트를 어떻게 수행하는지
* 원활한 `협업`을 위한 `코딩 컨벤션`, `문서화`, `github 소통(이슈, PR)`은 어떻게 해야 하는지

실제 서비스의 실무 상황을 가정하며 프로젝트를 진행하였습니다. 

<br>
<br>





### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 프로젝트 구성도

<img src="https://user-images.githubusercontent.com/71416000/268016745-c0fb78fa-9f0d-4b96-9eaf-70ca58d629f5.jpg">

<br>
<br>





<div><h3><img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 핵심 기능 API 시퀀스 다이어그램</h3></div>

<img src="https://user-images.githubusercontent.com/71416000/267723109-7d29d56a-1c8d-488a-80cd-2dc926b4ef5b.png">
<img src="https://user-images.githubusercontent.com/71416000/267723147-66db4f06-50ff-4523-859f-e9a1dc8e00ca.png">
<br>
<br>





### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 주요 기술적 고민과 문제 해결 과정

<br>
<br>





### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> API 문서

<br>
<br>





### <img src="https://user-images.githubusercontent.com/71416000/267310457-c5136192-dbbe-4466-b02a-6b73f6a31e93.png" width="3%"/> 스키마 ERD

<img src="https://user-images.githubusercontent.com/71416000/268024847-4ad2a1b6-e58d-47e6-9968-081226443b9a.png">
