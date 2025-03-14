package com.exam.service;

import com.exam.dal.dto.ExamRequest;
import com.exam.dal.model.*;
import com.exam.dal.repository.CourseRepository;
import com.exam.dal.repository.ExamRepository;
import com.exam.dal.repository.SubmissionRepository;
import com.exam.dal.repository.UserRepository;
import com.exam.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    /**
     * Gets all exams, filtered based on the user's role
     *
     * @return List of exams the user has access to
     */
    public List<Exam> getAllExams() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return examRepository.findAll();
        } else if (currentUser.getRole() == Role.TEACHER) {
            return examRepository.findAll().stream()
                    .filter(exam -> exam.getCourse().getTeacher().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        } else {
            return examRepository.findAll().stream()
                    .filter(Exam::isPublished)
                    .filter(exam -> exam.getCourse().getStudents().contains(currentUser))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Gets all exams created by the current teacher
     *
     * @return List of exams created by the teacher
     */
    public List<Exam> getTeacherExams() {
        User currentUser = getCurrentUser();
        return examRepository.findAll().stream()
                .filter(exam -> exam.getCourse().getTeacher().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all upcoming exams for the current student.
     * Upcoming exams are published exams that haven't started yet.
     *
     * @return List of upcoming exams
     */
    public List<Exam> getUpcomingExams() {
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        return examRepository.findAll().stream()
                .filter(Exam::isPublished)
                .filter(exam -> exam.getStartTime().isAfter(now))
                .filter(exam -> exam.getCourse().getStudents().contains(currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Gets all currently available exams for the current student.
     * Available exams are published exams that are currently running and haven't been submitted yet.
     *
     * @return List of available exams
     */
    public List<Exam> getAvailableExams() {
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        return examRepository.findAll().stream()
                .filter(Exam::isPublished)
                .filter(exam -> exam.getStartTime().isBefore(now) && exam.getEndTime().isAfter(now))
                .filter(exam -> exam.getCourse().getStudents().contains(currentUser))
                .filter(exam -> submissionRepository.findByStudentAndExam(currentUser, exam).isEmpty())
                .collect(Collectors.toList());
    }

//    /**
//     * Gets all completed exams with their results for the current student.
//     *
//     * @return List of maps containing exam and submission details
//     */
//    public List<Map<String, Object>> getCompletedExamsWithResults() {
//        User currentUser = getCurrentUser();
//        List<Submission> submissions = submissionRepository.findByStudent(currentUser);
//
//        List<Map<String, Object>> results = new ArrayList<>();
//
//        for (Submission submission : submissions) {
//            Exam exam = submission.getExam();
//
//            Map<String, Object> examResult = new HashMap<>();
//
//            // Create exam info
//            Map<String, Object> examInfo = new HashMap<>();
//            examInfo.put("id", exam.getId());
//            examInfo.put("title", exam.getTitle());
//
//            // Create course info
//            Map<String, Object> courseInfo = new HashMap<>();
//            courseInfo.put("id", exam.getCourse().getId());
//            courseInfo.put("title", exam.getCourse().getTitle());
//            examInfo.put("course", courseInfo);
//
//            // Create submission info
//            Map<String, Object> submissionInfo = new HashMap<>();
//            submissionInfo.put("id", submission.getId());
//            submissionInfo.put("submittedAt", submission.getSubmittedAt());
//            submissionInfo.put("graded", submission.isGraded());
//            submissionInfo.put("score", submission.getScore());
//
//            // Add to result
//            examResult.put("exam", examInfo);
//            examResult.put("submission", submissionInfo);
//
//            results.add(examResult);
//        }
//
//        return results;
//    }

    /**
     * Gets an exam by its ID
     *
     * @param id The exam ID
     * @return The exam entity
     * @throws ResourceNotFoundException if the exam is not found
     */
    public Exam getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));

        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN ||
                exam.getCourse().getTeacher().getId().equals(currentUser.getId()) ||
                (exam.isPublished() && exam.getCourse().getStudents().contains(currentUser))) {
            return exam;
        } else {
            throw new RuntimeException("You don't have permission to view this exam");
        }
    }

    /**
     * Gets all exams for a specific course
     *
     * @param courseId The course ID
     * @return List of exams
     */
    public List<Exam> getExamsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN ||
                course.getTeacher().getId().equals(currentUser.getId())) {
            return examRepository.findByCourse(course);
        } else if (course.getStudents().contains(currentUser)) {
            return examRepository.findByCourseAndPublishedTrue(course);
        } else {
            throw new RuntimeException("You are not enrolled in this course");
        }
    }

    /**
     * Creates a new exam
     *
     * @param examRequest The exam creation data
     * @return The created exam
     * @throws ResourceNotFoundException if the course is not found
     * @throws RuntimeException if the user doesn't have permission
     */
    public Exam createExam(ExamRequest examRequest) {
        User currentUser = getCurrentUser();

        Course course = courseRepository.findById(examRequest.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + examRequest.getCourseId()));

        if (!course.getTeacher().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You don't have permission to create exams for this course");
        }

        Exam exam = Exam.builder()
                .title(examRequest.getTitle())
                .description(examRequest.getDescription())
                .startTime(examRequest.getStartTime())
                .endTime(examRequest.getEndTime())
                .durationMinutes(examRequest.getDurationMinutes())
                .published(false)
                .course(course)
                .build();

        return examRepository.save(exam);
    }

    /**
     * Updates an existing exam
     *
     * @param id The exam ID
     * @param examRequest The updated exam data
     * @return The updated exam
     * @throws ResourceNotFoundException if the exam or course is not found
     * @throws RuntimeException if the user doesn't have permission
     */
    public Exam updateExam(Long id, ExamRequest examRequest) {
        Exam exam = getExamById(id);
        User currentUser = getCurrentUser();

        if (!exam.getCourse().getTeacher().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You don't have permission to update this exam");
        }

        // If course is changing, verify permissions
        if (!exam.getCourse().getId().equals(examRequest.getCourseId())) {
            Course newCourse = courseRepository.findById(examRequest.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + examRequest.getCourseId()));

            if (!newCourse.getTeacher().getId().equals(currentUser.getId()) &&
                    currentUser.getRole() != Role.ADMIN) {
                throw new RuntimeException("You don't have permission to move this exam to the specified course");
            }

            exam.setCourse(newCourse);
        }

        exam.setTitle(examRequest.getTitle());
        exam.setDescription(examRequest.getDescription());
        exam.setStartTime(examRequest.getStartTime());
        exam.setEndTime(examRequest.getEndTime());
        exam.setDurationMinutes(examRequest.getDurationMinutes());

        return examRepository.save(exam);
    }

    /**
     * Deletes an exam
     *
     * @param id The exam ID
     * @throws ResourceNotFoundException if the exam is not found
     * @throws RuntimeException if the user doesn't have permission
     */
    public void deleteExam(Long id) {
        Exam exam = getExamById(id);
        User currentUser = getCurrentUser();

        if (!exam.getCourse().getTeacher().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You don't have permission to delete this exam");
        }

        examRepository.delete(exam);
    }

    /**
     * Publishes an exam, making it visible to students
     *
     * @param id The exam ID
     * @return The published exam
     * @throws ResourceNotFoundException if the exam is not found
     * @throws RuntimeException if the user doesn't have permission or the exam has no questions
     */
    public Exam publishExam(Long id) {
        Exam exam = getExamById(id);
        User currentUser = getCurrentUser();

        if (!exam.getCourse().getTeacher().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You don't have permission to publish this exam");
        }

        if (exam.getQuestions().isEmpty()) {
            throw new RuntimeException("Cannot publish an exam with no questions");
        }

        exam.setPublished(true);
        return examRepository.save(exam);
    }

    /**
     * Unpublishes an exam, hiding it from students
     *
     * @param id The exam ID
     * @return The unpublished exam
     * @throws ResourceNotFoundException if the exam is not found
     * @throws RuntimeException if the user doesn't have permission
     */
    public Exam unpublishExam(Long id) {
        Exam exam = getExamById(id);
        User currentUser = getCurrentUser();

        if (!exam.getCourse().getTeacher().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You don't have permission to unpublish this exam");
        }

        exam.setPublished(false);
        return examRepository.save(exam);
    }

    /**
     * Gets the currently authenticated user
     *
     * @return The user entity
     * @throws RuntimeException if the user is not found
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    /**
     * Gets all completed exams with their results for the current student.
     *
     * @return List of maps containing exam and submission details
     */
    public List<Map<String, Object>> getCompletedExamsWithResults() {
        User currentUser = getCurrentUser();
        List<Submission> submissions = submissionRepository.findByStudent(currentUser);

        List<Map<String, Object>> results = new ArrayList<>();

        for (Submission submission : submissions) {
            Exam exam = submission.getExam();

            Map<String, Object> examResult = new HashMap<>();

            // Create exam info
            Map<String, Object> examInfo = new HashMap<>();
            examInfo.put("id", exam.getId());
            examInfo.put("title", exam.getTitle());

            // Create course info
            Map<String, Object> courseInfo = new HashMap<>();
            courseInfo.put("id", exam.getCourse().getId());
            courseInfo.put("title", exam.getCourse().getTitle());
            examInfo.put("course", courseInfo);

            // Create submission info
            Map<String, Object> submissionInfo = new HashMap<>();
            submissionInfo.put("id", submission.getId());
            submissionInfo.put("submittedAt", submission.getSubmittedAt());
            submissionInfo.put("graded", submission.isGraded());

            // Handle Score details
            Score score = submission.getScore();
            if (score != null) {
                Map<String, Object> scoreInfo = new HashMap<>();
                scoreInfo.put("totalScore", score.getTotalScore());
                scoreInfo.put("percentageScore", score.getPercentageScore());
                scoreInfo.put("reading", score.getReading());
                scoreInfo.put("status", score.getStatus());
                scoreInfo.put("gradedAt", score.getGradedAt());

                submissionInfo.put("score", scoreInfo);
            }

            // Add to result
            examResult.put("exam", examInfo);
            examResult.put("submission", submissionInfo);

            results.add(examResult);
        }

        return results;
    }
}