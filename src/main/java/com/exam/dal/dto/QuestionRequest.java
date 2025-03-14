package com.exam.dal.dto;


import com.exam.dal.model.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Marks is required")
    @Positive(message = "Marks must be positive")
    private Integer marks;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @Valid
    private List<OptionRequest> options;
}