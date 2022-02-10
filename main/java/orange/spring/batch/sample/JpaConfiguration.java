package orange.spring.batch.sample;

import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orange.spring.batch.entity.Progress;
import orange.spring.batch.repository.ProgressRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JpaConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	private final ProgressRepository progressRepository;
	
	@Bean
	public Job jpaJob() throws Exception {
		return jobBuilderFactory.get("jpaJob")
				.incrementer(new RunIdIncrementer())
				.start(this.jpaStep())
				.build();
	}
	
	@Bean
	public Step jpaStep() throws Exception {
		return stepBuilderFactory.get("jpaStep")
				.<Progress, Progress>chunk(10)
                .reader(this.jpaCursorItemReader())
                .writer(itemWriter())
				.build();
	}
	
    private JpaCursorItemReader<Progress> jpaCursorItemReader() throws Exception {
        JpaCursorItemReader<Progress> itemReader = new JpaCursorItemReaderBuilder<Progress>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from Progress p")
                .build();
        itemReader.afterPropertiesSet();

        return itemReader;
    }
    
    private ItemWriter<Progress> itemWriter() {
        return items -> log.info(items.stream()
                .map(Progress::getName)
                .collect(Collectors.joining(", ")));
    }

}
