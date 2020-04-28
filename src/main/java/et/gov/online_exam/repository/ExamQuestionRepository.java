package et.gov.online_exam.repository;

import org.springframework.data.repository.CrudRepository;

import et.gov.online_exam.entity.ExamQuestion;

public interface ExamQuestionRepository extends CrudRepository<ExamQuestion, Integer> {

}
