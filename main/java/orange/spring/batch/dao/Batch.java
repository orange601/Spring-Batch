package orange.spring.batch.dao;

import java.util.List;
import java.util.Map;

public interface Batch {
	public List<Object> findAllFiles(Map<String, Object> params);

}
