package orange.spring.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.annotation.BeforeStep;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProgressListener {
    public static class ProgressStepListener {
        @BeforeStep
        public void beforeStep(StepExecution stepExecution) {
            log.info("beforeStep");
        }

        @AfterStep
        public ExitStatus afterStep(StepExecution stepExecution) {
            log.info("Step Write Count : {}", stepExecution.getWriteCount());
            long time = stepExecution.getEndTime().getTime() - stepExecution.getStartTime().getTime();
            log.info("Total Step Processing Time {}millis", time);
            return stepExecution.getExitStatus();
        }
    }
    
    public static class ProgressJobListener {
        @BeforeJob
        public void beforeJob(JobExecution jobExecution) {
            log.info("BeforeJob");
        }

        @AfterJob
        public void afterJob(JobExecution jobExecution) {
            int sum = jobExecution.getStepExecutions().stream()
                    .mapToInt(StepExecution::getWriteCount)
                    .sum();
            log.info("Job Write Count : {}", sum);
            
            long time = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
            log.info("Total Job Processing Time {}millis", time);
        }
    }

}
