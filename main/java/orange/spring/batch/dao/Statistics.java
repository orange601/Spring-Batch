package orange.spring.batch.dao;

import java.util.List;
import java.util.Map;

import orange.spring.batch.entity.Progress;

public interface Statistics {
	public List<Progress> findAllProgress(Map<String, Object> params);
	
	List<Statistics> findStatistics(Map<String, Object> params);

}
