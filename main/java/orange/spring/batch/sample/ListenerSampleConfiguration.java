package orange.spring.batch.sample;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orange.spring.batch.entity.Progress;

/**
 * listener 샘플 테스트
 * @category 리스너를 테스트 하기 위한 객체
 * */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ListenerSampleConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
    @Bean
    public Job sampleListenerJob() {
        return jobBuilderFactory.get("sampleListenerJob")
                .incrementer(new RunIdIncrementer())
                .start(this.sampleListenerStep())
				.listener(new SampleListener.SampleJobListener())
				.listener(new SampleListener.SampleJobAnnotaionListener())
                .build();
    }
    
    @Bean
    public Step sampleListenerStep() {
        return stepBuilderFactory.get("sampleListenerStep")
				.<Progress, Progress>chunk(10)
                .reader(itemReader())
                .writer(itemWriter())
                .listener(new SampleListener.SampleStepListener())
                .listener(new SampleListener.SampleStepAnnotationListener())
				.build();

    }
    
    private ItemReader<Progress> itemReader() {
    	return new ListItemReader<>(getItems());
    }
    private ItemWriter<Progress> itemWriter() {
    	return items -> log.info("chunk item size : {}", items.size());
    }
    private List<Progress> getItems() {
        List<Progress> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
        	Progress progress = new Progress();
        	progress.setName(i + " Hello");
            items.add(progress);
        }
        return items;
    }
}
