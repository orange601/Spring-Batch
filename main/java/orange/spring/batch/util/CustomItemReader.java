package orange.spring.batch.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * 현재 사용 안 하는중
 * */
public class CustomItemReader<T> implements ItemReader<T> {
    private final List<T> items;

    public CustomItemReader(List<T> items) {
        this.items = new ArrayList<>(items);
    }

	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!items.isEmpty()) {
            return items.remove(0);
        }

        return null; // null을 리턴하면 reader가 종료된다는 의미다
	}

}
