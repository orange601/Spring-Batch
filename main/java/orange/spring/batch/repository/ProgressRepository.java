package orange.spring.batch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import orange.spring.batch.entity.Progress;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
	List<Progress> findAll();
	
}
