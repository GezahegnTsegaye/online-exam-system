package com.exam.dal.mapper;


import com.exam.dal.dto.QuestionDto;
import com.exam.dal.entity.Question;
import org.mapstruct.Mapper;

@Mapper
public interface QuestionMapper {
    Question toQuestion(QuestionDto questionDto);
    QuestionDto toQuestionDto(Question question);
}
