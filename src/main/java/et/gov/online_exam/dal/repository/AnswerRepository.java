package et.gov.online_exam.dal.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import et.gov.online_exam.dal.entity.Answer;
import et.gov.online_exam.dal.entity.Question;

public interface AnswerRepository extends CrudRepository<Answer, Long> {
	
	/*
	List<Answer> findByQuestion(Question question);
	
	List<Answer> findByQuestionId(Integer questionId);
	
	
	List<Answer> findByQuestionExamIdAndCorrect(Integer examId, Boolean isCorrect);*/

}
