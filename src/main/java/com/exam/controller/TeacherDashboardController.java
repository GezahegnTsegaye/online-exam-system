package com.exam.controller;

import com.exam.dal.model.Course;
import com.exam.dal.model.Exam;
import com.exam.dal.model.Submission;
import com.exam.service.CourseService;
import com.exam.service.ExamService;
import com.exam.service.SubmissionService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher/dashboard")
@PreAuthorize("hasAuthority('TEACHER')")
@RequiredArgsConstructor
@Validated
@Slf4j
public class TeacherDashboardController {

    private final CourseService courseService;
    private final ExamService examService;
    private final SubmissionService submissionService;

    /**
     * Retrieve courses taught by the current teacher
     * @return List of courses
     */
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getTeacherCourses() {
        log.info("Fetching courses for current teacher");
        List<Course> courses = courseService.getTeacherCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * Retrieve exams created by the current teacher
     * @return List of exams
     */
    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getTeacherExams() {
        log.info("Fetching exams for current teacher");
        List<Exam> exams = examService.getTeacherExams();
        return ResponseEntity.ok(exams);
    }

    /**
     * Get student progress for a specific course
     * @param courseId The ID of the course
     * @return List of student progress details
     */
    @GetMapping("/course/{courseId}/students")
    public ResponseEntity<List<Map<String, Object>>> getCourseStudentsWithProgress(
            @PathVariable @Positive(message = "Course ID must be a positive number") Long courseId) {
        log.info("Fetching student progress for course with ID: {}", courseId);
        List<Map<String, Object>> studentProgress = courseService.getCourseStudentsWithProgress(courseId);
        return ResponseEntity.ok(studentProgress);
    }

    /**
     * Get submissions for a specific exam
     * @param examId The ID of the exam
     * @return List of submissions
     */
    @GetMapping("/exam/{examId}/submissions")
    public ResponseEntity<List<Submission>> getExamSubmissions(
            @PathVariable @Positive(message = "Exam ID must be a positive number") Long examId) {
        log.info("Fetching submissions for exam with ID: {}", examId);
        List<Submission> submissions = submissionService.getSubmissionsByExam(examId);
        return ResponseEntity.ok(submissions);
    }

    /**
     * Get comprehensive exam statistics
     * @param examId The ID of the exam
     * @return Exam statistics
     */
    @GetMapping("/exam/{examId}/stats")
    public ResponseEntity<Map<String, Object>> getExamStats(
            @PathVariable @Positive(message = "Exam ID must be a positive number") Long examId) {
        log.info("Calculating statistics for exam with ID: {}", examId);

        // Fetch submissions
        List<Submission> submissions = submissionService.getSubmissionsByExam(examId);

        // Calculate statistics with more robust handling
        Map<String, Object> stats = calculateExamStatistics(submissions);

        return ResponseEntity.ok(stats);
    }

    /**
     * Calculate exam statistics
     * @param submissions List of submissions
     * @return Map of exam statistics
     */
    private Map<String, Object> calculateExamStatistics(List<Submission> submissions) {
        // Filter graded submissions
        List<Submission> gradedSubmissions = submissions.stream()
                .filter(Submission::isGraded)
                .collect(Collectors.toList());

        // Calculate statistics
        return Map.of(
                "totalSubmissions", submissions.size(),
                "gradedSubmissions", gradedSubmissions.size(),
                "averageScore", calculateAverageScore(gradedSubmissions),
                "highestScore", findHighestScore(gradedSubmissions),
                "lowestScore", findLowestScore(gradedSubmissions),
                "pendingGrading", submissions.stream().filter(s -> !s.isGraded()).count()
        );
    }

    /**
     * Calculate average score for graded submissions
     * @param gradedSubmissions List of graded submissions
     * @return Average score
     */
    private double calculateAverageScore(List<Submission> gradedSubmissions) {
        return gradedSubmissions.stream()
                .mapToDouble(submission ->
                        submission.getScore() != null ?
                                submission.getScore().getTotalScore() : 0.0)
                .average()
                .orElse(0.0);
    }

    /**
     * Find highest score among graded submissions
     * @param gradedSubmissions List of graded submissions
     * @return Highest score
     */
    private double findHighestScore(List<Submission> gradedSubmissions) {
        return gradedSubmissions.stream()
                .mapToDouble(submission ->
                        submission.getScore() != null ?
                                submission.getScore().getTotalScore() : 0.0)
                .max()
                .orElse(0.0);
    }

    /**
     * Find lowest score among graded submissions
     * @param gradedSubmissions List of graded submissions
     * @return Lowest score
     */
    private double findLowestScore(List<Submission> gradedSubmissions) {
        return gradedSubmissions.stream()
                .mapToDouble(submission ->
                        submission.getScore() != null ?
                                submission.getScore().getTotalScore() : 0.0)
                .min()
                .orElse(0.0);
    }
}