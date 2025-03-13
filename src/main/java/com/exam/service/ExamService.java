package com.exam.service;

import com.exam.dal.dto.ExamQuestionDto;

import javax.transaction.Transactional;

public interface ExamService {
    @Transactional
    void insertExam(ExamQuestionDto examQuestionDto);

	/*ExamQuestion getRandomExam();

    ExamQuestion insertExam(ExamQuestion exam);*/

//	List<Question> getQuestionsForExam(int examId);

}
