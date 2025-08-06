# Findex

## 💫 팀원 소개
| 김이준 | 문은서 | 신은수 | 김찬호 | 신동진 |
| :---: | :---: | :---: | :---: | :---: |
| [![김이준](https://avatars.githubusercontent.com/u/93887188?v=4)](https://github.com/lkim0402) | [![문은서](https://avatars.githubusercontent.com/u/191211966?v=4)](https://github.com/kosy00) | [![신은수](https://avatars.githubusercontent.com/u/94344629?v=4)](https://github.com/Shinsu98) | [![김찬호](https://avatars.githubusercontent.com/u/106953872?v=4)](https://github.com/cheis11) | [![신동진](https://avatars.githubusercontent.com/u/135810601?v=4)](https://github.com/B1uffer) |

## 📌프로젝트 소개
한눈에 보는 금융 지수 데이터 대시보드 서비스

프로젝트 기간: 2025. 07.28. ~ 2025. 08.06.

## 프로젝트 다이어그램
![Findex Diagram](src/main/resources/static/findex_diagram.png "Diagram")

# 🤖기술 스택
## 백엔드 & 데이터베이스
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white)
![SpringBoot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=Spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
- `Spring Data JPA`, `springdoc-openapi`, `mapstruct` 사용

## 테스팅 툴
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white)

## 배포
![Railway](https://img.shields.io/badge/Railway-0B0D12?style=for-the-badge&logo=railway&logoColor=white)

## 협업 툴
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white)

# 💡팀원별 구현 기능 상세
## 문은서 - 지수 정보 관리
### 지수 정보 등록
- 지수 분류명, 지수명, 채용 종목 수, 기준 시점, 기준 지수, 즐겨찾기를 통해 지수 정보를 등록하는 기능 구현
- 채용 종목 수, 기준 시점, 기준 지수, 즐겨찾기를 통해 지수 정보를 등록하는 기능 구현

### 지수 정보 수정
- 채용 종목 수, 기준 시점, 기준 지수, 즐겨찾기을 수정할 수 있는 기능 구현
- 채용 종목 수, 기준 시, 기준 지수는 Open API를 활용해 자동으로 수정할 수 있게끔 구현

### 지수 정보 삭제
- 지수 정보를 삭제하면 관련된 지수 데이터도 같이 삭제되는 기능 구현

### 지수 정보 목록 조회
- 지수 분류명, 지수명, 즐겨찾기로 지수 정보 목록을 조회할 수 있는 기능 구현.
  - 조회 조건이 여러 개인 경우 모든 조건을 만족한 결과로 조회


- 지수 분류명, 지수명, 채용 종목 수로 정렬 및 페이지네이션을 구현.
  - nextIdAfter, nextCursor를 활용하여 페이지네이션 구현

## 신동진 - 지수 데이터 관리
### 지수 데이터 등록
- 지수 정보의 외래키와 기준일자를 바탕으로 지수 데이터를 등록할 수 있는 기능
  - 지수 정보, 기준일자는 중복되면 안되게끔 구현
### 지수 데이터 수정
- 지수 정보, 기준일자를 제외한 모든 속성을 수정할 수 있는 기능
### 지수 데이터 삭제
- 지수 데이터를 선택하여 삭제할 수 있는 기능
### 지수 데이터 조회
- 지수 정보, 기준일자를 바탕으로 지수 데이터 목록을 조회할 수 있는 기능
  - 소스 타입을 제외한 모든 속성으로 정렬 및 페이지네이션 구현
### 지수 데이터 Export
- 생성한 지수 데이터들을 CSV파일로 Export(다운로드) 할 수 있는 기능 구현
  - Export할 지수 데이터를 지수 데이터 목록 조회와 같은 규칙으로 필터링, 정렬기능 구현

## 김찬호 - 연동 작업 관리
### 지수 정보 연동
- Open API를 활용해 지수 정보를 등록, 수정
- 지수 정보 연동은 사용자가 직접 실행 가능

### 지수 데이터 연동
- Open API를 활용해 지수 데이터를 등록, 수정
- 지수, 대상 날짜로 연동할 데이터의 범위를 지정

### 연동 작업 목록 조회
- 유형, 지수, 대상 날짜, 작업자, 결과, 작업일시로 연동 작업 목록을 조회할 수 있는 기능 구현
- 대상 날짜, 작업일시으로 정렬 및 페이지네이션을 구현

## 신은수 - 자동 연동 설정 관리
### 자동 연동 설정 등록
- 지수 분류명, 지수명, 채용 종목 수, 기준 시점, 기준 지수 등의 모든 속성을 통해 자동 연동 설정을 등록할 수 있는 기능 구현
- 지수 정보 등록에서 지수가 등록될 때 지수 연동이 비활성화 상태로 등록되도록 하는 기능 구현

### 자동 연동 설정 수정
- 자동 연동을 활성화/비활성화 전환 기능을 구현

### 자동 연동 설정 목록 조회
- {지수}, {활성화}를 포함한 정보로 자동 연동 설정 목록을 조회하는 기능을 구현
  - 조회 조건이 여러 개인 경우 모든 조건을 만족한 결과로 조회하도록 구현
- {지수}, {활성화}로 정렬 및 페이지네이션을 구현
  - 여러 개의 정렬 조건 중 선택적으로 1개의 정렬 조건만 가질 수 있도록 구현
  - 정확한 페이지네이션을 위해 {이전 페이지의 마지막 요소 ID}를 활용

### 배치에 의한 지수 데이터 연동 자동화
- 지수 데이터 연동 프로세스를 일정한 주기(1일)마다 자동으로 반복하도록 구현
  - 배치 주기는 애플리케이션 설정(.yml)을 통해 주입하도록 설정
  - Spring Scheduler를 활용해 데이터 연동 자동화를 구현
- 데이터를 연동할 지수는 지수 연동 설정이 {활성화}로 변경되어 있는 것만 가능하도록 구현
- 연동할 {대상 날짜}는 해당 지수의 마지막 자동 연동 작업 날짜부터 가장 최신 날짜까지로 설정

## 김이준 - 대시보드 관리

### 주요 지수 현황 요약
- 즐겨찾기된 지수의 성과 정보를 포함하도록 기능 구현 
  - 성과는 종가를 기준으로 비교하는 로직 구현 
  - 성능 향상을 위해 @Query를 사용해서 적절한 SQL문 작성
### 지수 차트
- 월/분기/년 단위 시계열 데이터 사용 
- 이동평균선 데이터 로직 구현 
- 종가를 기준으로 전 5일/20일 데이터의 평균값을 내는 로직 구현
### 지수 성과 분석 랭킹
- 전일/전주/전월 대비 성과 랭킹 기능 구현 
- 성과는 종가를 기준으로 비교하는 로직 구현 
- 성능 향상을 위해 @Query를 사용해서 적절한 SQL문 작성

# 📋 파일 구조
```text
sb4-findex-team3 [findex]
├─ .github                        
├─ .gradle                        
├─ .idea                          
├─ build                          
├─ gradle                         
├─ out                            
├─ src                            
│  ├─ main
│  │  ├─ generated                
│  │  ├─ java
│  │  │  └─ com
│  │  │     └─ codeit
│  │  │        └─ findex
│  │  │           ├─ controller
│  │  │           │  └─ api
│  │  │           │     ├─ AutoSyncConfigController.java
│  │  │           │     ├─ DashboardController.java
│  │  │           │     ├─ ExternalApiController.java
│  │  │           │     ├─ IndexDataController.java
│  │  │           │     ├─ IndexInfoController.java
│  │  │           │     └─ IntergrationController.java
│  │  │           ├─ dto
│  │  │           │  ├─ autosync
│  │  │           │  │  ├─ request
│  │  │           │  │  │  └─ AutoSyncConfigUpdateRequest.java
│  │  │           │  │  └─ response
│  │  │           │  │     ├─ AutoSyncConfigDto.java
│  │  │           │  │     └─ CursorPageResponseAutoSyncConfigDto.java
│  │  │           │  ├─ dashboard
│  │  │           │  │  └─ response
│  │  │           │  │     └─ ChartDataPoint.java
│  │  │           │  ├─ indexData
│  │  │           │  │  ├─ request
│  │  │           │  │  │  ├─ IndexDataCreateRequest.java
│  │  │           │  │  │  ├─ IndexDataDateRequest.java
│  │  │           │  │  │  ├─ IndexDataDownloadRequest.java
│  │  │           │  │  │  ├─ IndexDataSearchRequest.java
│  │  │           │  │  │  ├─ IndexDataSortPageRequest.java
│  │  │           │  │  │  └─ IndexDataUpdateRequest.java
│  │  │           │  │  └─ response
│  │  │           │  │     ├─ CursorPageResponseIndexDataDto.java
│  │  │           │  │     └─ IndexDataDto.java
│  │  │           │  ├─ indexinfo
│  │  │           │  │  ├─ request
│  │  │           │  │  │  ├─ IndexInfoCreateRequest.java
│  │  │           │  │  │  ├─ IndexInfoSearchCond.java
│  │  │           │  │  │  └─ IndexInfoUpdateRequest.java
│  │  │           │  │  └─ response
│  │  │           │  │     ├─ CursorPageResponseIndexInfoDto.java
│  │  │           │  │     ├─ ErrorResponse.java
│  │  │           │  │     ├─ IndexInfoDto.java
│  │  │           │  │     └─ IndexInfoSummaryDto.java
│  │  │           │  ├─ integration
│  │  │           │  │  ├─ CursorPageResponseSyncJobDto.java
│  │  │           │  │  ├─ IndexDataSyncRequest.java
│  │  │           │  │  └─ SyncJobDto.java
│  │  │           │  └─ openapi
│  │  │           │     └─ OpenApiResponseDto.java
│  │  │           ├─ entity
│  │  │           │  ├─ base
│  │  │           │  │  ├─ IndexData.java
│  │  │           │  │  ├─ IndexInfo.java
│  │  │           │  │  └─ Integration.java
│  │  │           │  └─ entityEnum
│  │  │           │     ├─ JobType.java
│  │  │           │     ├─ Result.java
│  │  │           │     └─ SourceType.java
│  │  │           ├─ exception
│  │  │           │  └─ GlobalExceptionHandler.java
│  │  │           ├─ mapper
│  │  │           │  ├─ AutoSyncMapper.java
│  │  │           │  ├─ CSVStringMapper.java
│  │  │           │  ├─ GenericMapper.java
│  │  │           │  ├─ IndexDataIntegrationMapper.java
│  │  │           │  ├─ IndexDataMapper.java
│  │  │           │  ├─ IndexInfoMapper.java
│  │  │           │  └─ IntegrationMapper.java
│  │  │           ├─ repository
│  │  │           │  ├─ custom
│  │  │           │  │  └─ IntegrationCustomRepositoryImpl.java
│  │  │           │  ├─ AutoSyncRepository.java
│  │  │           │  ├─ DashboardRepository.java
│  │  │           │  ├─ IndexDataRepository.java
│  │  │           │  ├─ IndexInfoRepository.java
│  │  │           │  ├─ IntegrationCustomRepository.java
│  │  │           │  └─ IntegrationRepository.java
│  │  │           ├─ service
│  │  │           │  ├─ autosync
│  │  │           │  │  └─ basic
│  │  │           │  │     ├─ BasicAutoSyncConfigService.java
│  │  │           │  │     └─ AutoSyncConfigService.java
│  │  │           │  ├─ dashboard
│  │  │           │  │  └─ basic
│  │  │           │  │     ├─ BasicDashboardService.java
│  │  │           │  │     └─ DashboardService.java
│  │  │           │  ├─ indexdata
│  │  │           │  │  └─ basic
│  │  │           │  │     ├─ BasicIndexDataService.java
│  │  │           │  │     └─ IndexDataService.java
│  │  │           │  ├─ indexinfo
│  │  │           │  │  └─ basic
│  │  │           │  │     ├─ BasicIndexInfoService.java
│  │  │           │  │     └─ IndexInfoService.java
│  │  │           │  └─ integration
│  │  │           │     └─ basic
│  │  │           │        ├─ BasicIntegrationService.java
│  │  │           │        └─ IntegrationService.java
│  │  │           ├─ ExternalApiService.java
│  │  │           └─ FindexApplication.java
│  │  └─ resources
│  │     ├─ static
│  │     │  └─ assets
│  │     │     ├─ favicon.ico
│  │     │     ├─ findex_diagram.png
│  │     │     └─ index.html
│  │     ├─ application.yml
│  │     ├─ application-secret.yml
│  │     └─ schema.sql
│  ├─ querydsl
│  └─ test
├─ build.gradle
├─ gradlew
├─ gradlew.bat
├─ README.md
├─ settings.gradle
└─ .gitignore
````

# 🚀구현 홈페이지
(링크, 스크린샷 추가)
프로젝트 회고록

정
