package com.exam.service.impl;


import com.exam.dal.dto.QuestionDto;
import com.exam.dal.entity.Question;
import com.exam.dal.mapper.QuestionMapper;
import com.exam.dal.repository.ExamRepository;
import com.exam.dal.repository.QuestionRepository;
import com.exam.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {



    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionRepository questionRepository, ExamRepository examRepository, QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.questionMapper = questionMapper;
    }

    public Question insertQuestion(QuestionDto questionDto) {

        Optional<Question> questions = questionRepository.findById(questionDto.getQuestionId());
        Question question = new Question();
        if(questions.isPresent()){
            question = questionMapper.toQuestion(questionDto);
        }
        return question;
    }
}
