package orange.spring.batch.sample;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orange.spring.batch.dto.Statistics;
import orange.spring.batch.listener.StatisticsListener;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class StatisticConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final SqlSessionFactory sqlSessionFactory;
	
	private final int chunkSize = 10;
	
    @Bean
    public Job statisticJob() throws Exception {
        return jobBuilderFactory.get("statisticJob")
                .incrementer(new RunIdIncrementer())
                .start(this.statisticStep())
                .listener(new StatisticsListener.StatisticsJobListener())
                .build();
    }
    
    @Bean
    @JobScope
    public Step statisticStep() throws Exception {
        return stepBuilderFactory.get("statisticStep")
				.<Statistics, Statistics>chunk(chunkSize)
                .reader(statisticsItemReader())
                .writer(statisticsItemWriter())
                .listener(new StatisticsListener.StatisticsStepListener())
				.build();
    }
    
    @Bean
    @StepScope
    public MyBatisPagingItemReader<Statistics> statisticsItemReader() throws Exception {
    	//Map<String, Object> parameterValues = new HashMap<>();
    	//parameterValues.put("seq", 6);
    	return new MyBatisPagingItemReaderBuilder<Statistics>()
    			.pageSize(chunkSize)
    			.sqlSessionFactory(sqlSessionFactory)
    			.queryId("StatisticsMapper.findStatistics")
    			//.parameterValues(parameterValues)
    			.build();
    }
    
    /**
     * @category 데이터를 파일로 저장한다.
     * @apiNote 같은 이름의 파일이 생성되면 기존에 파일은 삭제되고, 새 파일이 생성된다.
     * */
    private ItemWriter<Statistics> statisticsItemWriter() throws Exception {
    	LocalDate localDate = LocalDate.now();
    	
    	DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
    	DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");
    	
    	String month = localDate.format(monthFormatter);
    	String day = localDate.format(dayFormatter);
    	
        String fileName = localDate.getYear() + "-" + month + "-" + day + ".csv";
        
        BeanWrapperFieldExtractor<Statistics> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"zoneVal"});
        
        DelimitedLineAggregator<Statistics> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);
        
        FlatFileItemWriter<Statistics> itemWriter = new FlatFileItemWriterBuilder<Statistics>()
                .resource(new FileSystemResource("output/" + fileName))
                .lineAggregator(lineAggregator)
                .name("statisticsItemWriter")
                .encoding("UTF-8")
                .build();
        itemWriter.afterPropertiesSet();
        
    	return itemWriter;
    }

}
