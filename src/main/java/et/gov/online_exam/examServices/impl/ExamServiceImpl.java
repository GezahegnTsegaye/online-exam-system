package et.gov.online_exam.examServices.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import et.gov.online_exam.entity.Answer;
import et.gov.online_exam.entity.Exam;
import et.gov.online_exam.entity.Question;
import et.gov.online_exam.entity.User;
import et.gov.online_exam.examServices.ExamService;
import et.gov.online_exam.repository.AnswerRepository;
import et.gov.online_exam.repository.ExamRepository;
import et.gov.online_exam.repository.ExamReviewerRepository;
import et.gov.online_exam.repository.QuestionRepository;

@Service
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ExamRepository examRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private ExamReviewerRepository examReviewerRepository;

	public static final double MAX_GRADE = 100; // 100 points

	@Override
	public Exam getRandomExam() {
		final int examCount = (int) examRepository.count();
		assert examCount > 0;
		final int examNumber = new Random().nextInt(examCount);
		return examRepository.findAll().g;
	}

	@Override
	@Transactional
	public long insertExam(Exam exam) {
		Long[] ids = exam.getIds();
		
		final Exam p = examRepository.save(exam);
		double score = 0;
		
		 if (ids != null) {
	            //è®¾ç½®examå’Œquestionçš„å…³è�”
	            for (Long id : ids) {
	                Exam exam = new Exam();
	                exam.setExamId(exam.getExamId());
	                exam.setQuestionId(id);
	                examQuestionDao.insert(exam);
	                //è®¾ç½®è¯•å�·çš„æ€»åˆ†
	                Question question = questionDao.queryById(id);
	                score += question.getScore();
	            }
	        }
	        //æ›´æ–°å…¶æ€»åˆ†
	        examDao.updateScoreById(score, exam.getExamId());
	        exam.setScore(score);
	        //èŽ·å�–å�‚åŠ è€ƒè¯•çš„å­¦ç”Ÿçš„ä¿¡æ�¯
	        Integer[] studentIds = exam.getStudentIds();
	        if (studentIds != null) {
	            for (Integer studentId : studentIds) {
	                User examStudent = new User();
	                examStudent.setExamId(exam.getExamId());
	                examStudent.setStudentId(studentId);
	                examStudentDao.insert(examStudent);
	            }
	        }
	        return insert;
	}

	@Override
	public List<Question> getQuestionsForExam(int examId) {
		final List<Question> questions = questionRepository.findByExamId(examId);
		return questions;
	}
	
	
	
	
	
	
	

	public void calcGrade(Exam exam, int examId, List<Integer> userAnswers) {
		if (exam == null || userAnswers == null) {
			throw new IllegalArgumentException("Invalid parameters on GRADE call");
		}

		final List<Answer> correctAnswers = answerRepository.findByQuestionExamIdAndCorrect(examId, true);
		if (correctAnswers.size() == 0) {
			throw new IllegalArgumentException("You must specify correct answers!");
		}

		final float step = (float) (MAX_GRADE / correctAnswers.size());
		final Set<Integer> correctCount = new HashSet<>();
		final Set<Integer> incorrectCount = new HashSet<>();

		final Set<Integer> answers = userAnswers.stream().filter(a -> a > 0).collect(Collectors.toSet());

		float grade = 0;
		for (final Answer answer : correctAnswers) {
			final Long id = (answer.getAnswerId());
			if (answers.contains(id)) {
				grade += step;
				correctCount.addAll(id);
			} else {
				incorrectCount.add(id);
			}
		}
		// fix for multi-answers questions
		correctCount.removeAll(incorrectCount);

		// grade
		exam.setCorrectAnswers(correctCount.size());
		exam.setGrade(Math.round(grade));
	}

}
