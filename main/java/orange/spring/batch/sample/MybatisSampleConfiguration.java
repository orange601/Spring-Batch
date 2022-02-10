package orange.spring.batch.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orange.spring.batch.entity.Progress;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MybatisSampleConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	public final SqlSessionFactory sqlSessionFactory;
	
	private final int chunkSize = 10;
	
    @Bean
    public Job mybatisSampleJob() throws Exception {
        return jobBuilderFactory.get("mybatisSampleJob")
                .incrementer(new RunIdIncrementer())
                .start(this.mybatisSampleStep())
                .build();
    }
    
    @Bean
    public Step mybatisSampleStep() throws Exception {
        return stepBuilderFactory.get("mybatisSampleStep")
				.<Progress, Progress>chunk(chunkSize)
                .reader(mybastisItemReader())
                .writer(mybatisItemWriter())
				.build();
    }
    
    @Bean
    @StepScope
    public MyBatisPagingItemReader<Progress> mybastisItemReader() throws Exception {
    	//Map<String, Object> parameterValues = new HashMap<>();
    	//parameterValues.put("seq", 6);
    	return new MyBatisPagingItemReaderBuilder<Progress>()
    			.pageSize(chunkSize)
    			.sqlSessionFactory(sqlSessionFactory)
    			.queryId("StatisticsMapper.findAllProgress")
    			//.parameterValues(parameterValues)
    			.build();
    }
    
    private ItemWriter<Progress> mybatisItemWriter() {
    	 return items -> items.forEach(x -> log.info("Progress name : {}", x.getName()));
    }

}
