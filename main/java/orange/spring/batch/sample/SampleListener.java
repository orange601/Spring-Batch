package orange.spring.batch.sample;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.annotation.BeforeStep;

import lombok.extern.slf4j.Slf4j;

/**
 * - Listener란 MVC에서 interceptor 개념이라고 생각하면 된다.
 * - 구현방법은 2가지 , 동작은 똑같음
 * - 1. Interface구현
 * - 2. Annotation 기반으로 구현
 * */
@Slf4j
public class SampleListener {
    /**
     * @category Step Listener를 interface로 구현 
     * @note SampleStepAnnotationListener 함수와 역할은 같고, 구현하는 방식만 다르다. 
     * */
	public static class SampleStepListener implements StepExecutionListener {
		@Override
		public void beforeStep(StepExecution stepExecution) {
			log.info("beforeStep");
		}

		@Override
		public ExitStatus afterStep(StepExecution stepExecution) {
            log.info("afterStep : {}", stepExecution.getWriteCount());
            return stepExecution.getExitStatus();
		}
		
	}
	
    /**
     * @category Step Listener를 Annotation으로 구현 
     * @note SampleStepListener 함수와 역할은 같고, 구현하는 방식만 다르다. 
     * */
    public static class SampleStepAnnotationListener {
        @BeforeStep
        public void beforeStep(StepExecution stepExecution) {
            log.info("beforeStep");
        }

        @AfterStep
        public ExitStatus afterStep(StepExecution stepExecution) {
            log.info("afterStep : {}", stepExecution.getWriteCount());
            return stepExecution.getExitStatus();
        }
    }
    
    /**
     * @category JOB Listener를 interface로 구현 
     * @note SampleJobAnnotaionListener 함수와 역할은 같고, 구현하는 방식만 다르다. 
     * */
    public static class SampleJobListener implements JobExecutionListener {
        @Override
        public void beforeJob(JobExecution jobExecution) {
        	// job 실행 전 처리
            log.info("beforeJob");
        }
        @Override
        public void afterJob(JobExecution jobExecution) {
        	// job 실행 후 처리
            int sum = jobExecution.getStepExecutions().stream()
                    .mapToInt(StepExecution::getWriteCount)
                    .sum();
            log.info("afterJob : {}", sum);
        }
    }
    
    /**
     * @category JOB Listener를 Annotation로 구현 
     * @note SampleJobListener 함수와 역할은 같고, 구현하는 방식만 다르다. 
     * */
    public static class SampleJobAnnotaionListener {
        @BeforeJob
        public void beforeJob(JobExecution jobExecution) {
            log.info("annotationBeforeJob");
        }

        @AfterJob
        public void afterJob(JobExecution jobExecution) {
            int sum = jobExecution.getStepExecutions().stream()
                    .mapToInt(StepExecution::getWriteCount)
                    .sum();
            log.info("annotationAfterJob : {}", sum);
            
            long time = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
            log.info("총 처리 시간 {}millis", time);
        }
    }

}
