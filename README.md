# BATCH_PROJECT
STRING_BATCH-배치프로그램

https://github.com/woniper/fastcampus-spring-batch-example 참조했음

## 1. 배치란? ##
  - 큰 단위의 작업을 일괄처리
  - 대부분 대용량 처리가 많음 (비실시간처리에 사용)
  - 컴퓨터 자원 사용이 낮은 시간대에 배치를 처리하는 것을 활용
  - **스케줄러와 혼동 금지 스케줄러는 JOB을 실행시키는 프로그램을 의미**

## 2. Spring Batch ##
  - 배치처리를 위해 Spring Framework 기반 기술 활용
  - 스프링배치의 실행 단위 **JOB**과 **Step**
  - JOB은 배치의 실행단위
  - Step은 JOB의 실행단위 - tasklet 객체 실행함
  - **간단한 작업: Tasklet 단위처리**
  - **대량 묶음처리: Chunk 단위처리**

----------------------------------------------------------

### 2.1 Spring Batch 기본 구조 ###
![이미지 2](https://user-images.githubusercontent.com/24876345/151474710-34fa570d-a366-4e35-9324-800a790a9c89.png)

----------------------------------------------------------

### 2.2 JOB ###
- Job은 JobLauncher에 의해 실행
- Job은 배치의 실행 단위를 의미
- Job은 N개의 Step을 실행할 수 있으며, 흐름(Flow)을 관리할 수 있다.
    - 예를 들면, A Step 실행 후 조건에 따라 B Step 또는 C Step을 실행 설정

### 2.3 STEP ###
- Step은 Job의 세부 실행 단위이며, N개가 등록돼 실행된다.
- Step의 실행 단위는 크게 2가지로 나눌 수 있다.
  1. Chunk 기반 : 하나의 큰 덩어리를 n개씩 나눠서 실행
  2. Task 기반 : 하나의 작업 기반으로 실행
- Chunk 기반 Step은 ItemReader, ItemProcessor, ItemWriter가 있다.
    - 여기서 Item은 배치 처리 대상 객체를 의미한다.
- **ItemReader**는 배치 처리 대상 객체를 읽어 ItemProcessor 또는 ItemWriter에게 전달한다.
    - 예를 들면, 파일 또는 **DB에서 데이터를 읽는다.**
- **ItemProcessor**는 input 객체를 output 객체로 filtering 또는 processing 해 ItemWriter에게 전달한다.
    - 예를 들면, ItemReader에서 **읽은 데이터를 수정** 또는 ItemWriter 대상인지 filtering 한다.
    - ItemProcessor는 optional 하다.
    - ItemProcessor가 하는 일을 ItemReader 또는 ItemWriter가 대신할 수 있다.
    - ![이미지1](https://user-images.githubusercontent.com/24876345/152299072-1b9879c5-6531-48bd-bd5f-e08339657748.png)
- **ItemWrite**r는 배치 처리 대상 객체를 처리한다.
    - 예를 들면, **DB update를 하거나, 처리 대상 사용자에게 알림**을 보낸다.

### 2.4 Chunk와 Step의 차이점 ###
  - chunk-size를 정하면 사이즈 개수만큼 작업단위 혹은 데이터를 나누어서 실행 (대용량 데이터에 적합)
  - Step으로도 작업 단위 혹은 데이터를 나누어서 실행 가능하지만 코드가 길어지고 복잡해진다.

----------------------------------------------------------

## 3. 배치 실행을 위한 메타 데이터가 저장되는 테이블 ##
![이미지 3](https://user-images.githubusercontent.com/24876345/151476669-1253fe05-d522-41fa-a704-0583f18df05c.png)

----------------------------------------------------------

- BATCH_JOB_INSTANCE
    - Job이 실행되며 생성되는 최상위 계층의 테이블
    - job_name과 job_key를 기준으로 하나의 row가 생성되며, 같은 job_name과 job_key가 저장될 수 없다.
    - job_key는 BATCH_JOB_EXECUTION_PARAMS에 저장되는 Parameter를 나열해 암호화해 저장한다.
- BATCH_JOB_EXECUTION
    - Job이 실행되는 동안 시작/종료 시간, job 상태 등을 관리
- BATCH_JOB_EXECUTION_PARAMS
    - Job을 실행하기 위해 주입된 parameter 정보 저장
- BATCH_JOB_EXECUTION_CONTEXT
    - Job이 실행되며 공유해야할 데이터를  직렬화해 저장
- BATCH_STEP_EXECUTION
    - Step이 실행되는 동안 필요한 데이터 또는 실행된 결과 저장
- BATCH_STEP_EXECUTION_CONTEXT
    - Step이 실행되며 공유해야할 데이터를 직렬화해 저장

### 3.1 테이블 수동 생성방법 ###
![이미지 1](https://user-images.githubusercontent.com/24876345/151293695-5aeed262-cd5f-425b-9304-c2a1d737846e.png)
- BATCH JAR파일을 확인해보면 DB별로 시키마 생성쿼리가 저장 되어 있다. 복사해서 스크립트로 바로 사용하면 된다.(이미지참조)
- spring-batch-core/org.springframework/batch/core/* 에 위치

## 4. PROPERTIES 설정 ##
  - Batch를 실행시키기 위해 기본적으로 필요한 테이블이 있는데 아래 설정을 해두면 자동으로 spring batch core에서 테이블을 생성한다.
  - spring.batch.jdbc.initialize-schema=always
  - option은 3가지 NAVER, ALWAYS, EMBEDDED
  - 테스트 환경에서는 ALWAYS나 EMBEDDED로 자동 생성을 하는것이 편하지만 
  - 운영환경에서는 DDL을 권한을 주는 것이므로 NEVER로 설정하고 수동으로 테이블을 생성한다.  
  - 설정하지 않으면 java.lang.IllegalStateException: Failed to execute ApplicationRunner 1차적으로 배치 실행이 에러가 발생
  - 2차적으로 "테이블 또는 뷰가 존재하지 않습니다" 에러 발생





