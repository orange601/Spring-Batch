package orange.spring.batch.support;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orange.spring.batch.part3.Person;

import static orange.spring.batch.part3.QPerson.person;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonRepositorySupport {
	private final JPAQueryFactory query;

    public JPAQuery<Person> findAll() {
        return query
                .selectFrom(person);
    }

}
