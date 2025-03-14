package com.exam.service;

import com.exam.dal.dto.AnswerRequest;
import com.exam.dal.dto.SubmissionRequest;
import com.exam.dal.model.*;
import com.exam.dal.repository.*;
import com.exam.exception.ResourceNotFoundException;
import com.exam.exception.UnauthorizedAccessException;
import com.exam.exception.ValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final ScoreRepository scoreRepository;

    /**
     * Get submissions for the current student
     *
     * @return List of submissions
     */
    @Transactional(readOnly = true)
    public List<Submission> getMySubmissions() {
        User currentUser = getCurrentUser();
        return submissionRepository.findByStudent(currentUser);
    }

    /**
     * Get submissions for a specific exam
     *
     * @param examId The exam ID
     * @return List of submissions
     */
    @Transactional(readOnly = true)
    public List<Submission> getSubmissionsByExam(Long examId) {
        // Fetch exam and validate access
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        User currentUser = getCurrentUser();

        // Check if user has permission to view submissions
        validateSubmissionViewAccess(currentUser, exam);

        return submissionRepository.findByExam(exam);
    }

    /**
     * Submit an exam
     *
     * @param submissionRequest Submission details
     * @return Created submission
     */
    @Transactional
    public Submission submitExam(SubmissionRequest submissionRequest) {
        User currentUser = validateStudentSubmission(submissionRequest);

        Exam exam = validateExamSubmission(currentUser, submissionRequest);

        // Validate answers before creating submission
        validateAnswers(exam, submissionRequest.getAnswers());

        // Create submission
        Submission submission = createSubmission(currentUser, exam);

        // Create and save answers
        Set<Answer> answers = createAnswersForSubmission(submission, submissionRequest.getAnswers());
        submission.setAnswers(answers);

        // Auto-grade if possible
        autoGradeSubmission(submission);

        return submissionRepository.save(submission);
    }

    /**
     * Grade a submission
     *
     * @param submissionId Submission ID
     * @param score Score to assign
     * @return Updated submission
     */
    @Transactional
    public Submission gradeSubmission(Long submissionId, Double score) {
        Submission submission = getSubmissionById(submissionId);
        User currentUser = getCurrentUser();

        // Validate grading access
        validateGradingAccess(currentUser, submission);

        // Validate score
        validateScore(submission, score);

        // Create or update score
        Score submissionScore = createOrUpdateScore(submission, score, currentUser);

        // Mark submission as graded
        submission.setGraded(true);
        submission.setScore(submissionScore);

        return submissionRepository.save(submission);
    }

    /**
     * Validate student can submit the exam
     */
    private User validateStudentSubmission(SubmissionRequest submissionRequest) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.STUDENT) {
            throw new UnauthorizedAccessException("Only students can submit exams");
        }

        return currentUser;
    }

    /**
     * Validate exam submission conditions
     */
    private Exam validateExamSubmission(User currentUser, SubmissionRequest submissionRequest) {
        Exam exam = examRepository.findById(submissionRequest.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // Check exam publication status
        if (!exam.isPublished()) {
            throw new ValidationException("Cannot submit to an unpublished exam");
        }

        // Check student enrollment
        if (!exam.getCourse().getStudents().contains(currentUser)) {
            throw new UnauthorizedAccessException("You are not enrolled in this course");
        }

        // Check exam time window
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime())) {
            throw new ValidationException("Exam is not currently available for submission");
        }

        // Check duplicate submission
        submissionRepository.findByStudentAndExam(currentUser, exam)
                .ifPresent(s -> {
                    throw new ValidationException("You have already submitted this exam");
                });

        return exam;
    }

    /**
     * Create submission
     */
    private Submission createSubmission(User student, Exam exam) {
        return Submission.builder()
                .submittedAt(LocalDateTime.now())
                .student(student)
                .exam(exam)
                .status(SubmissionStatus.SUBMITTED)
                .build();
    }

    /**
     * Validate submission view access
     */
    private void validateSubmissionViewAccess(User user, Exam exam) {
        if (!exam.getCourse().getTeacher().getId().equals(user.getId()) &&
                user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("You don't have permission to view submissions");
        }
    }

    /**
     * Validate grading access
     */
    private void validateGradingAccess(User user, Submission submission) {
        if (!submission.getExam().getCourse().getTeacher().getId().equals(user.getId()) &&
                user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("You don't have permission to grade this submission");
        }
    }

    /**
     * Validate score
     */
    private void validateScore(Submission submission, Double score) {
        double totalMarks = submission.getExam().getQuestions().stream()
                .mapToDouble(Question::getMarks)
                .sum();

        if (score < 0 || score > totalMarks) {
            throw new ValidationException("Score must be between 0 and " + totalMarks);
        }
    }

    /**
     * Create or update score
     */
    private Score createOrUpdateScore(Submission submission, Double score, User gradedBy) {
        // Find existing score or create new
        Score submissionScore = scoreRepository.findBySubmission(submission)
                .orElse(new Score());

        submissionScore.setTotalScore(score);
        submissionScore.setSubmission(submission);
        submissionScore.setGradedBy(gradedBy);

        // Calculate percentage and other details
        submissionScore.calculatePercentageScore();

        return scoreRepository.save(submissionScore);
    }

    // Existing helper methods like validateAnswers, createAnswersForSubmission, etc.
    // would remain largely the same, with minor adjustments for error handling

    /**
     * Get current authenticated user
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

    /**
     * Validate the answers in a submission request
     *
     * @param exam The exam being answered
     * @param answerRequests The answers provided
     * @throws ValidationException for validation errors
     */
    private void validateAnswers(Exam exam, List<AnswerRequest> answerRequests) {
        // Check if all required questions are answered
        Set<Long> examQuestionIds = exam.getQuestions().stream()
                .map(Question::getId)
                .collect(Collectors.toSet());

        Set<Long> answeredQuestionIds = answerRequests.stream()
                .map(AnswerRequest::getQuestionId)
                .collect(Collectors.toSet());

        if (!examQuestionIds.equals(answeredQuestionIds)) {
            throw new ValidationException("All questions must be answered");
        }

        // Validate each answer
        for (AnswerRequest answerRequest : answerRequests) {
            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + answerRequest.getQuestionId()));

            if (!question.getExam().getId().equals(exam.getId())) {
                throw new ValidationException("Question does not belong to this exam");
            }

            // Validate answer based on question type
            switch (question.getQuestionType()) {
                case ESSAY:
                    if (answerRequest.getTextAnswer() == null || answerRequest.getTextAnswer().isBlank()) {
                        throw new ValidationException("Essay questions require a text answer");
                    }
                    break;
                case MULTIPLE_CHOICE:
                    if (answerRequest.getSelectedOptionIds() == null || answerRequest.getSelectedOptionIds().isEmpty()) {
                        throw new ValidationException("Multiple choice questions require at least one selected option");
                    }
                    validateOptions(question, answerRequest.getSelectedOptionIds());
                    break;
                case SINGLE_CHOICE:
                case TRUE_FALSE:
                    if (answerRequest.getSelectedOptionIds() == null || answerRequest.getSelectedOptionIds().size() != 1) {
                        throw new ValidationException("Single choice and true/false questions require exactly one selected option");
                    }
                    validateOptions(question, answerRequest.getSelectedOptionIds());
                    break;
            }
        }
    }

    /**
     * Validate that selected options belong to the question
     *
     * @param question The question
     * @param selectedOptionIds The selected option IDs
     * @throws ValidationException if options are invalid
     */
    private void validateOptions(Question question, List<Long> selectedOptionIds) {
        // Check if all selected options belong to the question
        Set<Long> questionOptionIds = question.getOptions().stream()
                .map(Option::getId)
                .collect(Collectors.toSet());

        boolean allOptionsValid = selectedOptionIds.stream()
                .allMatch(questionOptionIds::contains);

        if (!allOptionsValid) {
            throw new ValidationException("Selected options must belong to the question");
        }
    }


    /**
     * Create answer entities for a submission
     *
     * @param submission The submission
     * @param answerRequests The answer requests
     * @return Set of created answers
     */
    private Set<Answer> createAnswersForSubmission(Submission submission, List<AnswerRequest> answerRequests) {
        Set<Answer> answers = new HashSet<>();

        for (AnswerRequest answerRequest : answerRequests) {
            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + answerRequest.getQuestionId()));

            // Create basic answer
            Answer answer = Answer.builder()
                    .textAnswer(answerRequest.getTextAnswer())
                    .submission(submission)
                    .question(question)
                    .build();

            // Handle selected options
            if (answerRequest.getSelectedOptionIds() != null && !answerRequest.getSelectedOptionIds().isEmpty()) {
                Set<Option> selectedOptions = answerRequest.getSelectedOptionIds().stream()
                        .map(optionId -> optionRepository.findById(optionId)
                                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId)))
                        .collect(Collectors.toSet());

                // Set selected options
                answer.setSelectedOptions(selectedOptions);
            }

            answers.add(answerRepository.save(answer));
        }

        return answers;
    }

    /**
     * Auto-grade submission for objective questions
     *
     * @param submission The submission to grade
     */
    private void autoGradeSubmission(Submission submission) {
        double totalScore = 0;

        for (Answer answer : submission.getAnswers()) {
            Question question = answer.getQuestion();

            // Skip essay questions
            if (question.getQuestionType() == QuestionType.ESSAY) {
                continue;
            }

            // Get correct options for this question
            Set<Long> correctOptionIds = question.getOptions().stream()
                    .filter(Option::isCorrect)
                    .map(Option::getId)
                    .collect(Collectors.toSet());

            // Get selected options for this answer
            Set<Long> selectedOptionIds = answer.getSelectedOptions().stream()
                    .map(Option::getId)
                    .collect(Collectors.toSet());

            // Grading logic for different question types
            switch (question.getQuestionType()) {
                case SINGLE_CHOICE:
                case TRUE_FALSE:
                    if (correctOptionIds.equals(selectedOptionIds)) {
                        totalScore += question.getMarks();
                    }
                    break;
                case MULTIPLE_CHOICE:
                    // Count correct selections (true positives)
                    int truePositives = 0;
                    for (Long selectedId : selectedOptionIds) {
                        if (correctOptionIds.contains(selectedId)) {
                            truePositives++;
                        }
                    }

                    // Count incorrect selections (false positives)
                    int falsePositives = selectedOptionIds.size() - truePositives;

                    // Count missed correct options (false negatives)
                    int falseNegatives = correctOptionIds.size() - truePositives;

                    // Calculate score based on accuracy
                    double accuracy = (double) truePositives / (truePositives + falsePositives + falseNegatives);
                    totalScore += Math.round(accuracy * question.getMarks());
                    break;
            }
        }

        // Create or update Score
        Score score = submission.getScore();
        if (score == null) {
            score = new Score();
            submission.setScore(score);
        }

        // Set total score
        score.setTotalScore(totalScore);

        // Calculate percentage and other details
        score.calculatePercentageScore();

        // Check if all questions are objective
        boolean allObjective = submission.getExam().getQuestions().stream()
                .noneMatch(q -> q.getQuestionType() == QuestionType.ESSAY);

        if (allObjective) {
            submission.setGraded(true);
        }
    }

    /**
     * Check if the question is non-objective (cannot be auto-graded)
     */
    private boolean isNonObjectiveQuestion(Question question) {
        return question.getQuestionType() == QuestionType.ESSAY ||
                question.getQuestionType() == QuestionType.SHORT_ANSWER;
    }

    /**
     * Get correct option IDs for a question
     */
    private Set<Long> getCorrectOptionIds(Question question) {
        return question.getOptions().stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .collect(Collectors.toSet());
    }

    /**
     * Get selected option IDs for an answer
     */
    private Set<Long> getSelectedOptionIds(Answer answer) {
        return answer.getSelectedOptions().stream()
                .map(Option::getId)
                .collect(Collectors.toSet());
    }

    /**
     * Calculate score for a specific question
     */
    private double calculateQuestionScore(Question question, Set<Long> correctOptionIds, Set<Long> selectedOptionIds) {
        switch (question.getQuestionType()) {
            case SINGLE_CHOICE:
            case TRUE_FALSE:
                return isSingleChoiceCorrect(correctOptionIds, selectedOptionIds) ?
                        question.getMarks() : 0;

            case MULTIPLE_CHOICE:
                return calculateMultipleChoiceScore(question, correctOptionIds, selectedOptionIds);

            default:
                return 0;
        }
    }

    /**
     * Check if single choice question is correct
     */
    private boolean isSingleChoiceCorrect(Set<Long> correctOptionIds, Set<Long> selectedOptionIds) {
        return correctOptionIds.equals(selectedOptionIds);
    }

    /**
     * Calculate score for multiple choice questions
     */
    private double calculateMultipleChoiceScore(Question question, Set<Long> correctOptionIds, Set<Long> selectedOptionIds) {
        // Count correct and incorrect selections
        int truePositives = (int) selectedOptionIds.stream()
                .filter(correctOptionIds::contains)
                .count();

        int falsePositives = selectedOptionIds.size() - truePositives;
        int falseNegatives = correctOptionIds.size() - truePositives;

        // Calculate accuracy
        double accuracy = calculateAccuracy(truePositives, falsePositives, falseNegatives);

        // Calculate proportional score
        return Math.round(accuracy * question.getMarks());
    }

    /**
     * Calculate accuracy for multiple choice questions
     */
    private double calculateAccuracy(int truePositives, int falsePositives, int falseNegatives) {
        // Prevent division by zero
        int totalSelections = truePositives + falsePositives + falseNegatives;
        return totalSelections > 0 ?
                (double) truePositives / totalSelections :
                0;
    }

    /**
     * Check if exam can be fully auto-graded
     */
    private boolean isFullyObjectiveExam(Submission submission) {
        return submission.getExam().getQuestions().stream()
                .noneMatch(this::isNonObjectiveQuestion);
    }
    /**
     * Get a submission by ID with access control
     *
     * @param id The submission ID
     * @return The submission
     * @throws ResourceNotFoundException if submission not found
     * @throws UnauthorizedAccessException if user lacks permission
     */
    public Submission getSubmissionById(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));

        User currentUser = getCurrentUser();

        // Check access permissions
        boolean hasAccess = submission.getStudent().getId().equals(currentUser.getId()) ||
                submission.getExam().getCourse().getTeacher().getId().equals(currentUser.getId()) ||
                currentUser.getRole() == Role.ADMIN;

        if (!hasAccess) {
            throw new UnauthorizedAccessException("You don't have permission to view this submission");
        }

        return submission;
    }

    public Map<String, Object> getSubmissionResults(Long submissionId) {
        Submission submission = getSubmissionById(submissionId);

        // Validate access to submission results
        validateSubmissionResultsAccess(submission);

        // Prepare results map
        Map<String, Object> results = new HashMap<>();

        // Add submission basic info
        results.put("submissionId", submission.getId());
        results.put("examTitle", submission.getExam().getTitle());

        // Add score details if available
        if (submission.getScore() != null) {
            Score score = submission.getScore();
            results.put("totalScore", score.getTotalScore());
            results.put("percentageScore", score.getPercentageScore());
            results.put("reading", score.getReading());
            results.put("status", score.getStatus());
        }

        // Add answer details (optional, depending on requirements)
        // results.put("answers", mapSubmissionAnswers(submission));

        return results;
    }

    private void validateSubmissionResultsAccess(Submission submission) {
        User currentUser = getCurrentUser();

        boolean hasAccess = submission.getStudent().getId().equals(currentUser.getId()) ||
                submission.getExam().getCourse().getTeacher().getId().equals(currentUser.getId()) ||
                currentUser.getRole() == Role.ADMIN;

        if (!hasAccess) {
            throw new UnauthorizedAccessException("You don't have permission to view this submission's results");
        }
    }
}