package et.gov.online_exam.examServices.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import et.gov.online_exam.dal.dto.ExamQuestionDto;
import et.gov.online_exam.dal.dto.QuestionDto;
import et.gov.online_exam.dal.entity.ExamQuestion;
import et.gov.online_exam.dal.entity.Question;
import et.gov.online_exam.dal.mapper.ExamQuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import et.gov.online_exam.examServices.ExamService;
import et.gov.online_exam.dal.repository.AnswerRepository;
import et.gov.online_exam.dal.repository.ExamQuestionRepository;
import et.gov.online_exam.dal.repository.QuestionRepository;

@Service
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ExamQuestionRepository examRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private ExamQuestionRepository examQuestionRepository;
	@Autowired
	private ExamQuestionMapper examQuestionMapper;

	public static final double MAX_GRADE = 100; // 100 points

//	@Override
//	public ExamQuestionDto getRandomExam() {
//		final int examCount = (int) examRepository.count();
//		assert examCount > 0;
//		final int examNumber = new Random().nextInt(examCount);
//
//		ExamQuestion exam = new ExamQuestion();
//		exam.setExamCode(Integer.valueOf(examNumber).toString());
//		return exam;
//	}

	@Override
	@Transactional
	public void insertExam(ExamQuestionDto examQuestionDto) {
		ExamQuestion examQuestion = examQuestionMapper.toExamQuestion(examQuestionDto);
		if(examQuestion != null){
		examRepository.save(examQuestion);}

	}

//	@Override
//	public List<QuestionDto> getQuestionsForExam(long examId) {
//		final Optional<Question> questions = questionRepository.findById(examId);
//
//		return questions;
//	}
	
	
	
	
	
//
//
//
//	public void calcGrade(Exam exam, int examId, List<Integer> userAnswers) {
//		if (exam == null || userAnswers == null) {
//			throw new IllegalArgumentException("Invalid parameters on GRADE call");
//		}
//
//		final List<Answer> correctAnswers = answerRepository.findByQuestionExamIdAndCorrect(examId, true);
//		if (correctAnswers.size() == 0) {
//			throw new IllegalArgumentException("You must specify correct answers!");
//		}
//
//		final float step = (float) (MAX_GRADE / correctAnswers.size());
//		final Set<Integer> correctCount = new HashSet<>();
//		final Set<Integer> incorrectCount = new HashSet<>();
//
//		final Set<Integer> answers = userAnswers.stream().filter(a -> a > 0).collect(Collectors.toSet());
//
//		float grade = 0;
//		for (final Answer answer : correctAnswers) {
//			final Long id = (answer.getAnswerId());
//			if (answers.contains(id)) {
//				grade += step;
////				correctCount.addAll(id);
//			} else {
////				incorrectCount.add(id);
//			}
//		}
//		// fix for multi-answers questions
//		correctCount.removeAll(incorrectCount);
//
//		// grade
//		exam.setCorrectAnswers(correctCount.size());
//		exam.setGrade(Math.round(grade));
//	}

}
