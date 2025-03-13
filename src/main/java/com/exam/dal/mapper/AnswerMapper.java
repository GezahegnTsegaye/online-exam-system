package com.exam.dal.mapper;

import com.exam.dal.dto.AnswerDto;
import com.exam.dal.entity.Answer;
import org.mapstruct.Mapper;

@Mapper
public interface AnswerMapper {

    Answer toAnswer(AnswerDto answerDto);
    AnswerDto toAnswerDto(Answer answer);
}
