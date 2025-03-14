package com.exam.service;

import com.exam.dal.dto.OptionRequest;
import com.exam.dal.dto.QuestionRequest;
import com.exam.dal.model.*;
import com.exam.dal.repository.ExamRepository;
import com.exam.dal.repository.OptionRepository;
import com.exam.dal.repository.QuestionRepository;
import com.exam.dal.repository.UserRepository;
import com.exam.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    public List<Question> getQuestionsByExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN ||
                exam.getCourse().getTeacher().getId().equals(currentUser.getId()) ||
                (exam.isPublished() && exam.getCourse().getStudents().contains(currentUser))) {
            return questionRepository.findByExam(exam);
        } else {
            throw new RuntimeException("You don't have permission to view questions for this exam");
        }
    }

    public Question getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN ||
                question.getExam().getCourse().getTeacher().getId().equals(currentUser.getId()) ||
                (question.getExam().isPublished() &&
                        question.getExam().getCourse().getStudents().contains(currentUser))) {
            return question;
        } else {
            throw new RuntimeException("You don't have permission to view this question");
        }
    }

    @Transactional
    public Question createQuestion(QuestionRequest questionRequest) {
        User currentUser = getCurrentUser();

        Exam exam = examRepository.findById(questionRequest.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + questionRequest.getExamId()));

        if (!exam.getCourse().getTeacher().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You don't have permission to create questions for this exam");
        }

        // Validate options based on question type
        validateOptions(questionRequest);

        Question question = Question.builder()
                .content(questionRequest.getContent())
                .marks(questionRequest.getMarks())
                .questionType(questionRequest.getQuestionType())
                .exam(exam)
                .options(new HashSet<>())
                .build();

        Question savedQuestion = questionRepository.save(question);

        // Create options if provided
        if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
            Set<Option> options = createOptionsForQuestion(savedQuestion, questionRequest.getOptions());
            savedQuestion.setOptions(options);
        }

        return savedQuestion;
    }

    @Transactional
    public Question updateQuestion(Long id, QuestionRequest questionRequest) {
        Question question = getQuestionById(id);
        User currentUser = getCurrentUser();

        if (!question.getExam().getCourse().getTeacher().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You don't have permission to update this question");
        }

        // Validate options based on question type
        validateOptions(questionRequest);

        // If exam is changing, verify permissions
        if (!question.getExam().getId().equals(questionRequest.getExamId())) {
            Exam newExam = examRepository.findById(questionRequest.getExamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + questionRequest.getExamId()));

            if (!newExam.getCourse().getTeacher().getId().equals(currentUser.getId()) &&
                    currentUser.getRole() != Role.ADMIN) {
                throw new RuntimeException("You don't have permission to move this question to the specified exam");
            }

            question.setExam(newExam);
        }

        question.setContent(questionRequest.getContent());
        question.setMarks(questionRequest.getMarks());
        question.setQuestionType(questionRequest.getQuestionType());

        // Update options
        if (questionRequest.getOptions() != null) {
            // Delete existing options
            optionRepository.deleteAll(question.getOptions());
            question.getOptions().clear();

            // Create new options
            Set<Option> newOptions = createOptionsForQuestion(question, questionRequest.getOptions());
            question.setOptions(newOptions);
        }

        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        Question question = getQuestionById(id);
        User currentUser = getCurrentUser();

        if (!question.getExam().getCourse().getTeacher().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You don't have permission to delete this question");
        }

        questionRepository.delete(question);
    }

    private void validateOptions(QuestionRequest questionRequest) {
        // For multiple choice and single choice questions, options are required
        if ((questionRequest.getQuestionType() == QuestionType.MULTIPLE_CHOICE ||
                questionRequest.getQuestionType() == QuestionType.SINGLE_CHOICE) &&
                (questionRequest.getOptions() == null || questionRequest.getOptions().isEmpty())) {
            throw new RuntimeException("Options are required for multiple/single choice questions");
        }

        // For true/false questions, verify exactly 2 options
        if (questionRequest.getQuestionType() == QuestionType.TRUE_FALSE &&
                (questionRequest.getOptions() == null || questionRequest.getOptions().size() != 2)) {
            throw new RuntimeException("True/False questions must have exactly 2 options");
        }

        // For single choice and true/false, verify exactly one correct option
        if ((questionRequest.getQuestionType() == QuestionType.SINGLE_CHOICE ||
                questionRequest.getQuestionType() == QuestionType.TRUE_FALSE) &&
                questionRequest.getOptions() != null) {

            long correctCount = questionRequest.getOptions().stream()
                    .filter(OptionRequest::getCorrect)
                    .count();

            if (correctCount != 1) {
                throw new RuntimeException("Single choice questions must have exactly one correct option");
            }
        }

        // For multiple choice, verify at least one correct option
        if (questionRequest.getQuestionType() == QuestionType.MULTIPLE_CHOICE &&
                questionRequest.getOptions() != null) {

            boolean hasCorrect = questionRequest.getOptions().stream()
                    .anyMatch(OptionRequest::getCorrect);

            if (!hasCorrect) {
                throw new RuntimeException("Multiple choice questions must have at least one correct option");
            }
        }
    }

    private Set<Option> createOptionsForQuestion(Question question, List<OptionRequest> optionRequests) {
        Set<Option> options = new HashSet<>();

        for (OptionRequest optionRequest : optionRequests) {
            Option option = Option.builder()
                    .content(optionRequest.getContent())
                    .correct(optionRequest.getCorrect())
                    .question(question)
                    .build();

            options.add(optionRepository.save(option));
        }

        return options;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}