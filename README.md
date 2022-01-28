# BATCH_PROJECT
STRING_BATCH-배치프로그램

## 1. 배치란? ##
  - 큰 단위의 작업을 일괄처리
  - 대부분 대용량 처리가 많음 (비실시간처리에 사용)
  - 컴퓨터 자원 사용이 낮은 시간대에 배치를 처리하는 것을 활용

## 2. Spring Batch ##
  - 배치처리를 위해 Spring Framework 기반 기술 활용
  - 스프링배치의 실행 단위 **JOB**과 **Step**
  - JOB은 배치의 실행단위
  - Step은 JOB의 실행단위 - tasklet 객체로 배치를 실행함
  - **간단한 작업: Tasklet 단위처리**
  - **대량 묶음처리: Chunk 단위처리**

## 2. Gradle dependency ##
- spring-boot-starter-batch
- spring-boot-starter-web

## 3. Chunk란? ##
  - chunk-size를 정하면 사이즈 개수만큼 작업단위 혹은 데이터를 나누어서 실행 (대용량 데이터에 적합)
  - Step은 작업단위 혹은 데이터를 나누어서 사용불가
  - Step으로도 작업 단위 혹은 데이터를 나누어서 실행 가능하지만 코드가 길어지고 복잡해진다.

## 4. PROPERTIES 설정 ##
  - Batch를 실행시키기 위해 기본적으로 필요한 테이블이 있는데 아래 설정을 해두면 자동으로 spring batch core에서 테이블을 생성한다.
  - spring.batch.jdbc.initialize-schema=always
  - option은 3가지 NAVER, ALWAYS, EMBEDDED
  - 테스트 환경에서는 ALWAYS나 EMBEDDED로 자동 생성을 하는것이 편하지만 
  - 운영환경에서는 DDL을 권한을 주는 것이므로 NEVER로 설정하고 수동으로 테이블을 생성한다.  
  - 설정하지 않으면 java.lang.IllegalStateException: Failed to execute ApplicationRunner 1차적으로 배치 실행이 에러가 발생
  - 2차적으로 "테이블 또는 뷰가 존재하지 않습니다" 에러 발생

## 5. 테이블 수동 생성방법
![이미지 1](https://user-images.githubusercontent.com/24876345/151293695-5aeed262-cd5f-425b-9304-c2a1d737846e.png)

  - BATCH JAR파일을 확인해보면 DB별로 시키마 생성쿼리가 저장 되어 있다. 복사해서 스크립트로 바로 사용하면 된다.(이미지참조)

## 6. Run Cofiguration ##
  - Arguments 설정 ( 어떤 job을 실행시킬건지 )
  - --job.name=helloJob


