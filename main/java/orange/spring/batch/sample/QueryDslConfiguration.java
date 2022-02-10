package orange.spring.batch.sample;

import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.querydsl.reader.QuerydslPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orange.spring.batch.entity.Progress;
import orange.spring.batch.support.ProgressRepositorySupport;

/**
 * Spring batch에서 querydsl을 지원하지않는다.
 * 그러므로, 배달의 민족에서 사용하는 소스를 지원받아 사용한다.
 * */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class QueryDslConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	private final ProgressRepositorySupport progressRepositorySupport;
	
	private final int chunkSize = 10;
	
	@Bean
	public Job queryDslJob() {
		return jobBuilderFactory.get("queryDslJob")
				.incrementer(new RunIdIncrementer())
				.start(this.querydslStep())
				.build();
	}
	
	@Bean
	public Step querydslStep() {
		return stepBuilderFactory.get("querydslStep")
				.<Progress, Progress>chunk(chunkSize)
                .reader(this.getItems())
                .writer(writer())
				.build();
	}
	
    @Bean
    public QuerydslPagingItemReader<Progress> getItems() {
        return new QuerydslPagingItemReader<>(entityManagerFactory, chunkSize, queryFactory -> progressRepositorySupport.findProgress());
    }
    
    private ItemWriter<Progress> writer() {
    	return items -> log.info(items.stream()
    			.map(Progress::getName)
    			.collect(Collectors.joining(", ")));
    }

}
