package com.exam.dal.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExamQuestionDto {


    private Long examQuestionId;
    private String name;
    private List<QuestionDto> questionDtos;
}
