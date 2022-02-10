package orange.spring.batch.part1;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class HelloConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	public HelloConfiguration(JobBuilderFactory jobBuilderFactory
			, StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}
	
	@Bean // job은 배치의 실행 단위
	public Job helloJob() {
		return jobBuilderFactory.get("helloJob")
				.incrementer(new RunIdIncrementer()) // job이 실행될때마다 파라미터 자동생성
				.start(this.helloStep()) // job 실행시 최소 실행
				.build();
	}
	
	@Bean // step은 job의 실행 단위라 생각하면 쉽다.
	public Step helloStep() {
		return stepBuilderFactory.get("helloStep")
				.tasklet((contribution, chunkContext) -> {
					log.info("Hello spring batch");
					return RepeatStatus.FINISHED;
				}).build();
	}

}
