package com.exam.service;

import com.exam.dal.entity.Answer;

import java.util.List;

public interface AnswerService {
    Answer insertAnswer(Answer answer);

    List<Answer> getAnswersList();

    Answer getAnswers(long answerId);
}
