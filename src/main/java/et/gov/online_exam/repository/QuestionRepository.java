package et.gov.online_exam.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import et.gov.online_exam.entity.Exam;
import et.gov.online_exam.entity.Question;


public interface QuestionRepository extends CrudRepository<Question, Long> {

	List<Question> findByExam(Exam exam);

	List<Question> findByExamId(Integer examId);

	// find first not answered question by exam
	Question findFirstByExamAndIdNotIn(Exam exam, Set<Long> ids);

	int countByExam(Exam exam);

}
