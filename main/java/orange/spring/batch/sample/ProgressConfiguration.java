package orange.spring.batch.sample;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.querydsl.reader.QuerydslPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orange.spring.batch.entity.Progress;
import orange.spring.batch.listener.ProgressListener;
import orange.spring.batch.part3.Person;
import orange.spring.batch.support.ProgressRepositorySupport;
import orange.spring.batch.util.ProgressHeaderFooterCallBack;
import orange.spring.batch.util.ProgressJsonItemAggregator;

/**
 * TODO: insert를 chunk로 나눌건지 select를 chunk로 나눌건지 둘다 나눌건지 성능에 도움되는 방향으로 확인필
 * */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProgressConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	//private final ProgressRepository progressRepository;
	private final ProgressRepositorySupport progressRepositorySupport;
	
	// TODO: chunk 사이즈는 몇개로 지정하는게 성능에 좋은지 확인필
	private final int chunkSize = 10;
	
	@Bean
	public Job statisticsJob() throws Exception {
		return jobBuilderFactory.get("progress")
				.incrementer(new RunIdIncrementer())
				.start(this.findAllProgressDataStep())
                //.listener(new ProgressListener.ProgressJobListener())
				.build();
	}
	
	/**
	 * @Note 대량처리는 Tasklet보다 쉽게 구현
	 * @Note <INPUT, OUTPUT>chunk(int)
	 * @Note 1.reader에서 input을 리턴
	 * @Note 2. process는 input을 받아 processing 후 output을 리턴
	 * @Note 3. writer는 List<output>을 받아 write
	 * */
	@Bean
	@JobScope // @Value("#{jobParameters[key]}" 를 사용하기 위해 jobscope step scope가 필요하다. >>  또한 하나의 job이 여러스텝을 사용할경우 쓰레드 세이프하게 사용가능하다.
	public Step findAllProgressDataStep() throws Exception {
		return stepBuilderFactory.get("findAllProgressDataStep")
				.<Progress, Progress>chunk(chunkSize) // /Reader의 반환타입 & Writer의 파라미터타입
                .reader(progressItemReader())
                //.processor(progressItemProcessor())
                .writer(jsonFileItemWriter())
                //.listener(new ProgressListener.ProgressStepListener())
				.build();
	}
	
	// TODO: chunksize만큼 통신(재호출)한다.. 이래도 되나.. 성능이슈 확인필 (connection 재연결??)
    @Bean
    public QuerydslPagingItemReader<Progress> progressItemReader() {
        return new QuerydslPagingItemReader<>(entityManagerFactory, chunkSize, queryFactory -> progressRepositorySupport.findProgress());
    }
    
    private ItemProcessor<? super Progress, ? extends Progress> progressItemProcessor() {
    	 return item -> {
             if (!item.getName().equals("")) {
            	 log.info("ZONE : {}", item);
                 return item;
             }
             return null;
         };
    }
    
    /**
     * @category json파일 만들때
     * */
    public ItemWriter<Progress> jsonFileItemWriter() throws Exception {
    	ProgressHeaderFooterCallBack progressHeaderFooterCallBack = new ProgressHeaderFooterCallBack();
    	String fileName = this.getFullPath("json");
        FlatFileItemWriter<Progress> itemWriter = new FlatFileItemWriterBuilder<Progress>()
                .resource(new FileSystemResource(fileName))
                .lineAggregator(new ProgressJsonItemAggregator<Progress>())
                .name("statisticsItemWriter")
                .shouldDeleteIfExists(true)
                //.headerCallback(progressHeaderFooterCallBack)
                .footerCallback(write -> write.write("]"))
                .encoding("UTF-8")
                //.lineSeparator(",")
                .build();
        
        itemWriter.afterPropertiesSet();
    	
    	return itemWriter;
    }
    
    /**
     * @category csv파일 만들때
     * */
    private ItemWriter<Progress> progressItemWriter() throws Exception {
    	LocalDate localDate = LocalDate.now();
    	
    	DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
    	DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");
    	
    	String month = localDate.format(monthFormatter);
    	String day = localDate.format(dayFormatter);
    	
        String fileName = localDate.getYear() + "-" + month + "-" + day + ".csv";
        
        BeanWrapperFieldExtractor<Progress> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"seq", "name"});
        
        DelimitedLineAggregator<Progress> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);
        
        FlatFileItemWriter<Progress> itemWriter = new FlatFileItemWriterBuilder<Progress>()
                .resource(new FileSystemResource("output/" + fileName))
                .lineAggregator(lineAggregator)
                .name("statisticsItemWriter")
                .encoding("UTF-8")
                .build();
        itemWriter.afterPropertiesSet();
        
    	return itemWriter;

    }
    
    private String getFullPath(String extension) {
    	String path = "Y:" + File.separator + "IDAS" + File.separator + "data"+ File.separator + "pcviewProgress" + File.separator;
    	LocalDate localDate = LocalDate.now();
    	DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
    	DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");
    	String month = localDate.format(monthFormatter);
    	String day = localDate.format(dayFormatter);
    	return path + localDate.getYear() + "-" + month + "-" + day + "." + extension;
    }
    
}
