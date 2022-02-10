package orange.spring.batch.part3;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;

public class TemplateConfiguration {
/*	
 * 
 * 
   
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    
    public TemplateConfiguration(JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory) {
    	this.jobBuilderFactory = jobBuilderFactory;
    	this.stepBuilderFactory = stepBuilderFactory;
    }
    
    @Bean
    public Job job() {
        return jobBuilderFactory.get("")
                .incrementer(new RunIdIncrementer())
                .start(this.step())
                .build();
    }
    
    @Bean
    public Step step() {
        return stepBuilderFactory.get("")
                .chunk()
                .reader()
                .processor()
                .writer()
                .build();
    }
    
    
*/

}
