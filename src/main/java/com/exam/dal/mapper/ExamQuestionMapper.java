package com.exam.dal.mapper;


import com.exam.dal.dto.ExamQuestionDto;
import com.exam.dal.entity.Exam;
import org.mapstruct.Mapper;

@Mapper
public interface ExamQuestionMapper {

    Exam toExamQuestion(ExamQuestionDto examQuestionDto);
ExamQuestionDto toExamQuestionDto(Exam exam);
}
