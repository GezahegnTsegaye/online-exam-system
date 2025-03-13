package com.exam.service.impl;


import com.exam.dal.entity.Answer;
import com.exam.dal.repository.AnswerRepository;
import com.exam.service.AnswerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository) {

        this.answerRepository = answerRepository;
    }

    @Override
    public Answer insertAnswer( Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public List<Answer> getAnswersList() {

        return (List<Answer>) answerRepository.findAll();
    }


    @Override
    public Answer getAnswers(long answerId) {
        Optional<Answer> answer = answerRepository.findById(answerId);
        if (answer.isPresent()) answer.get();
        return answer.get();
    }
}
