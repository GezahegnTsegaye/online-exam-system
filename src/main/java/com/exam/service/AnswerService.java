package com.exam.service;

import com.exam.dal.model.Answer;

import java.util.List;


import com.exam.dal.dto.AnswerRequest;
import com.exam.dal.model.*;
import com.exam.dal.repository.*;
import com.exam.exception.ResourceNotFoundException;
import com.exam.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final SubmissionRepository submissionRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    /**
     * Get answers for a specific submission
     * @param submissionId Submission ID
     * @return List of answers
     */
    @Transactional(readOnly = true)
    public List<Answer> getAnswersBySubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        // Validate access
        validateAnswerAccess(submission);

        return answerRepository.findBySubmission(submission);
    }

    /**
     * Create an answer for a submission
     * @param answerRequest Answer creation request
     * @return Created answer
     */
    @Transactional
    public Answer createAnswer(AnswerRequest answerRequest) {
        User currentUser = getCurrentUser();

        // Validate submission
        Submission submission = submissionRepository.findById(answerRequest.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        // Validate question
        Question question = questionRepository.findById(answerRequest.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Validate user has permission to answer
        if (!submission.getStudent().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to answer this submission");
        }

        // Create answer
        Answer answer = new Answer();
        answer.setSubmission(submission);
        answer.setQuestion(question);
        answer.setTextAnswer(answerRequest.getTextAnswer());

        // Handle selected options
        if (answerRequest.getSelectedOptionIds() != null && !answerRequest.getSelectedOptionIds().isEmpty()) {
            Set<Option> selectedOptions = answerRequest.getSelectedOptionIds().stream()
                    .map(optionId -> optionRepository.findById(optionId)
                            .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId)))
                    .collect(Collectors.toSet());

            answer.setSelectedOptions(selectedOptions);
        }

        return answerRepository.save(answer);
    }

    /**
     * Update an existing answer
     * @param answerId Answer ID
     * @param answerRequest Answer update request
     * @return Updated answer
     */
    @Transactional
    public Answer updateAnswer(Long answerId, AnswerRequest answerRequest) {
        // Find existing answer
        Answer existingAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        // Validate user access
        validateAnswerModificationAccess(existingAnswer);

        // Update text answer
        existingAnswer.setTextAnswer(answerRequest.getTextAnswer());

        // Update selected options
        if (answerRequest.getSelectedOptionIds() != null) {
            Set<Option> selectedOptions = answerRequest.getSelectedOptionIds().stream()
                    .map(optionId -> optionRepository.findById(optionId)
                            .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId)))
                    .collect(Collectors.toSet());

            existingAnswer.setSelectedOptions(selectedOptions);
        }

        return answerRepository.save(existingAnswer);
    }

    /**
     * Delete an answer
     * @param answerId Answer ID to delete
     */
    @Transactional
    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        // Validate user access
        validateAnswerModificationAccess(answer);

        answerRepository.delete(answer);
    }

    /**
     * Validate access to view answers
     * @param submission Submission to validate access for
     */
    private void validateAnswerAccess(Submission submission) {
        User currentUser = getCurrentUser();

        boolean hasAccess = submission.getStudent().getId().equals(currentUser.getId()) ||
                submission.getExam().getCourse().getTeacher().getId().equals(currentUser.getId()) ||
                currentUser.getRole() == Role.ADMIN;

        if (!hasAccess) {
            throw new UnauthorizedAccessException("You are not authorized to view these answers");
        }
    }

    /**
     * Validate access to modify an answer
     * @param answer Answer to validate modification access for
     */
    private void validateAnswerModificationAccess(Answer answer) {
        User currentUser = getCurrentUser();

        // Only the student who submitted the answer can modify it
        if (!answer.getSubmission().getStudent().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to modify this answer");
        }
    }

    /**
     * Get current authenticated user
     * @return Current user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("No authenticated user found");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}