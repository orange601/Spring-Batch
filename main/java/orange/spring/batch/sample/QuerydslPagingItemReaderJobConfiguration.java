package orange.spring.batch.sample;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.querydsl.reader.QuerydslNoOffsetPagingItemReader;
import org.springframework.batch.item.querydsl.reader.QuerydslPagingItemReader;
import org.springframework.batch.item.querydsl.reader.options.QuerydslNoOffsetNumberOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orange.spring.batch.part3.Person;
import orange.spring.batch.support.PersonRepositorySupport;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class QuerydslPagingItemReaderJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final PersonRepositorySupport personRepositorySupport;
    private final int chunkSize = 10;
    
    @Bean
    public Job querydslPagingJob() {
        return jobBuilderFactory.get("QUERYDSL_JOB2")
                .start(querydslPagingStep())
                .build();
    }

    @Bean
    public Step querydslPagingStep() {
        return stepBuilderFactory.get("QUERYDSL_STEP")
                .<Person, Person>chunk(chunkSize)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public QuerydslPagingItemReader<Person> reader() {
        return new QuerydslPagingItemReader<>(entityManagerFactory, chunkSize, queryFactory -> personRepositorySupport.findAll());
    }

    private ItemWriter<Person> writer() {
        return list -> {
            for (Person person: list) {
                log.info("person={}", person.getAge());
            }
        };
    }

}
