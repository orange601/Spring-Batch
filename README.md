# BATCH_PROJECT
STRING_BATCH-배치프로그램

## 배치란? ##
  - 일괄처리를 의미

## PROPERTIES 설정 ##
  - Batch를 실행시키기 위해 기본적으로 필요한 테이블이 있는데 아래 설정을 해두면 자동으로 spring batch core에서 테이블을 생성한다.
  - spring.batch.jdbc.initialize-schema=always
  - 설정하지 않으면 java.lang.IllegalStateException: Failed to execute ApplicationRunner 1차적으로 배치 실행이 에러가 발생
  - 2차적으로 "테이블 또는 뷰가 존재하지 않습니다" 에러 발생

## Run Cofiguration ##
  - Arguments 설정 ( 어떤 job을 실행시킬건지 )
  - --job.name=helloJob
