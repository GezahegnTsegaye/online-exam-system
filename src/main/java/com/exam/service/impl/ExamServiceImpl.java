package com.exam.service.impl;

import javax.transaction.Transactional;

import com.exam.dal.dto.ExamQuestionDto;
import com.exam.dal.entity.Exam;
import com.exam.dal.mapper.ExamQuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exam.service.ExamService;
import com.exam.dal.repository.AnswerRepository;
import com.exam.dal.repository.ExamRepository;
import com.exam.dal.repository.QuestionRepository;

@Service
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ExamRepository examRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private ExamRepository examRepository;
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
		Exam exam = examQuestionMapper.toExamQuestion(examQuestionDto);
		if(exam != null){
		examRepository.save(exam);}

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
