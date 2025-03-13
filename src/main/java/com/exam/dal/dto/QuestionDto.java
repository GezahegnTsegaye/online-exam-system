package com.exam.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;
@Data
public class QuestionDto {

    private Long questionId;
    private Integer numberOfPages;
    private String name;

    private String type;
    private String title;
    private Double score;
    private Double finalScore;
    @JsonProperty("answers")
    List<AnswerDto> answerDtos;
}
