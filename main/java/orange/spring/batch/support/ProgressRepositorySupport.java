package orange.spring.batch.support;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import orange.spring.batch.entity.Progress;

import static orange.spring.batch.entity.QProgress.progress;

@Repository
@RequiredArgsConstructor
public class ProgressRepositorySupport {
	private final JPAQueryFactory query;
	
    public JPAQuery<Progress> findProgress() {
        return query
                .selectFrom(progress);
    }

}
