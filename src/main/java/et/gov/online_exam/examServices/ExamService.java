package et.gov.online_exam.examServices;

import java.util.List;

import et.gov.online_exam.entity.Exam;
import et.gov.online_exam.entity.Question;

public interface ExamService {

	Exam getRandomExam();

 Exam insertExam(Exam exam);

	List<Question> getQuestionsForExam(int examId);

}
