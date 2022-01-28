package et.gov.online_exam.examServices;

import et.gov.online_exam.dal.dto.ExamQuestionDto;
import et.gov.online_exam.dal.entity.ExamQuestion;

import javax.transaction.Transactional;

public interface ExamService {
    @Transactional
    void insertExam(ExamQuestionDto examQuestionDto);

	/*ExamQuestion getRandomExam();

    ExamQuestion insertExam(ExamQuestion exam);*/

//	List<Question> getQuestionsForExam(int examId);

}
