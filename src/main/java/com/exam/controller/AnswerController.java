package com.exam.controller;

import com.exam.dal.dto.AnswerRequest;
import com.exam.dal.model.Answer;
import com.exam.service.AnswerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AnswerController {

    private final AnswerService answerService;

    /**
     * Get answers for a specific submission
     * @param submissionId Submission ID
     * @return List of answers
     */
    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<List<Answer>> getAnswersBySubmission(
            @PathVariable @Positive(message = "Submission ID must be a positive number") Long submissionId) {
        log.info("Fetching answers for submission with ID: {}", submissionId);
        return ResponseEntity.ok(answerService.getAnswersBySubmission(submissionId));
    }

    /**
     * Create a new answer
     * @param answerRequest Answer creation request
     * @return Created answer
     */
    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<Answer> createAnswer(
            @Valid @RequestBody AnswerRequest answerRequest) {
        log.info("Creating new answer for submission");
        return ResponseEntity.ok(answerService.createAnswer(answerRequest));
    }

    /**
     * Update an existing answer
     * @param answerId Answer ID
     * @param answerRequest Answer update request
     * @return Updated answer
     */
    @PutMapping("/{answerId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<Answer> updateAnswer(
            @PathVariable @Positive(message = "Answer ID must be a positive number") Long answerId,
            @Valid @RequestBody AnswerRequest answerRequest) {
        log.info("Updating answer with ID: {}", answerId);
        return ResponseEntity.ok(answerService.updateAnswer(answerId, answerRequest));
    }

    /**
     * Delete an answer
     * @param answerId Answer ID to delete
     * @return No content response
     */
    @DeleteMapping("/{answerId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable @Positive(message = "Answer ID must be a positive number") Long answerId) {
        log.info("Deleting answer with ID: {}", answerId);
        answerService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }
}