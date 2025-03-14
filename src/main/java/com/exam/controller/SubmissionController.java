package com.exam.controller;

import com.exam.dal.dto.SubmissionRequest;
import com.exam.dal.model.Submission;
import com.exam.service.SubmissionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SubmissionController {

    private final SubmissionService submissionService;

    /**
     * Get submissions for the current student
     * @return List of student's submissions
     */
    @GetMapping("/student")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<Submission>> getMySubmissions() {
        log.info("Fetching submissions for current student");
        return ResponseEntity.ok(submissionService.getMySubmissions());
    }

    /**
     * Get submissions for a specific exam
     * @param examId The exam ID
     * @return List of submissions for the exam
     */
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    public ResponseEntity<List<Submission>> getSubmissionsByExam(
            @PathVariable @Positive(message = "Exam ID must be a positive number") Long examId) {
        log.info("Fetching submissions for exam with ID: {}", examId);
        return ResponseEntity.ok(submissionService.getSubmissionsByExam(examId));
    }

    /**
     * Get a specific submission by ID
     * @param id The submission ID
     * @return The submission
     */
    @GetMapping("/{id}")
    public ResponseEntity<Submission> getSubmissionById(
            @PathVariable @Positive(message = "Submission ID must be a positive number") Long id) {
        log.info("Fetching submission with ID: {}", id);
        return ResponseEntity.ok(submissionService.getSubmissionById(id));
    }

    /**
     * Submit an exam
     * @param submissionRequest The submission details
     * @return The created submission
     */
    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<Submission> submitExam(
            @Valid @RequestBody SubmissionRequest submissionRequest) {
        log.info("Submitting exam for student");
        return ResponseEntity.ok(submissionService.submitExam(submissionRequest));
    }

    /**
     * Grade a submission
     * @param id The submission ID
     * @param score The score to assign
     * @return The graded submission
     */
    @PatchMapping("/{id}/grade")
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    public ResponseEntity<Submission> gradeSubmission(
            @PathVariable @Positive(message = "Submission ID must be a positive number") Long id,
            @RequestParam @Positive(message = "Score must be a positive number") Double score) {
        log.info("Grading submission with ID: {} with score: {}", id, score);
        return ResponseEntity.ok(submissionService.gradeSubmission(id, score));
    }

    /**
     * Get detailed submission results
     * @param id The submission ID
     * @return Detailed submission results
     */
    @GetMapping("/{id}/results")
    public ResponseEntity<Map<String, Object>> getSubmissionResults(
            @PathVariable @Positive(message = "Submission ID must be a positive number") Long id) {
        log.info("Fetching results for submission with ID: {}", id);
        return ResponseEntity.ok(submissionService.getSubmissionResults(id));
    }
}