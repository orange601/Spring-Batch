package orange.spring.batch.util;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.transform.LineAggregator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ProgressJsonItemAggregator<T> implements LineAggregator<T>, StepExecutionListener {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProgressJsonItemAggregator.class);
	
	private boolean isFirstObject = true;

	private ObjectMapper objectMapper = new ObjectMapper();
	{
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	@Override
	public String aggregate(T item) {
		String result = null;
		try {
			result = objectMapper.writeValueAsString(item);
			if (isFirstObject) {
				isFirstObject = false;
				return "[" + result;
			} else {
				return "," + result;
			}
		} catch (JsonProcessingException jpe) {
			logger.error("An error has occured. Error message {} ", jpe.getMessage() );
		}
		return result;
	}
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		stepExecution.getExecutionContext().putString("isFirstObject", Boolean.toString(isFirstObject));
		return null;
	}
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		if (stepExecution.getExecutionContext().containsKey("isFirstObject")) {
			isFirstObject = Boolean.parseBoolean(stepExecution.getExecutionContext().getString("isFirstObject"));
		}
	}


}
