package com.exam.controller;


import com.exam.dal.dto.QuestionRequest;
import com.exam.dal.model.Question;
import com.exam.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<Question>> getQuestionsByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(questionService.getQuestionsByExam(examId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        return ResponseEntity.ok(questionService.createQuestion(questionRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest questionRequest) {
        return ResponseEntity.ok(questionService.updateQuestion(id, questionRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}