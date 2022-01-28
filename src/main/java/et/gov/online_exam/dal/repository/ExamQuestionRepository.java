package et.gov.online_exam.dal.repository;

import org.springframework.data.repository.CrudRepository;

import et.gov.online_exam.dal.entity.ExamQuestion;

public interface ExamQuestionRepository extends CrudRepository<ExamQuestion, Integer> {

}
