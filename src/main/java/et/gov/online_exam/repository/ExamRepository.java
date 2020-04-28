package et.gov.online_exam.repository;

import org.springframework.data.repository.CrudRepository;

import et.gov.online_exam.entity.Exam;

public interface ExamRepository extends CrudRepository<Exam, Long> {
	
	

}
