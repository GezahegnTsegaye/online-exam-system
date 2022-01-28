package et.gov.online_exam.dal.repository;

import org.springframework.data.repository.CrudRepository;

import et.gov.online_exam.dal.entity.Question;


public interface QuestionRepository extends CrudRepository<Question, Long> {

//	List<Question> findByExamId(Integer examId);

	/*// find first not answered question by exam

	List<Question> findByExam(Exam exam);
	Question findFirstByExamAndIdNotIn(Exam exam, Set<Long> ids);

	int countByExam(Exam exam);*/

}
