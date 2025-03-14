package com.exam.controller;

import com.exam.dal.model.Course;
import com.exam.dal.model.Exam;
import com.exam.dal.model.Submission;
import com.exam.service.CourseService;
import com.exam.service.ExamService;
import com.exam.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/dashboard")
@PreAuthorize("hasAuthority('STUDENT')")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final CourseService courseService;
    private final ExamService examService;
    private final SubmissionService submissionService;

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getStudentCourses() {
        return ResponseEntity.ok(courseService.getStudentCourses());
    }

    @GetMapping("/exams/upcoming")
    public ResponseEntity<List<Exam>> getUpcomingExams() {
        return ResponseEntity.ok(examService.getUpcomingExams());
    }

    @GetMapping("/exams/available")
    public ResponseEntity<List<Exam>> getAvailableExams() {
        return ResponseEntity.ok(examService.getAvailableExams());
    }

    @GetMapping("/exams/completed")
    public ResponseEntity<List<Map<String, Object>>> getCompletedExams() {
        return ResponseEntity.ok(examService.getCompletedExamsWithResults());
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<Submission>> getMySubmissions() {
        return ResponseEntity.ok(submissionService.getMySubmissions());
    }

    @GetMapping("/course/{courseId}/progress")
    public ResponseEntity<Map<String, Object>> getCourseProgress(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getStudentCourseProgress(courseId));
    }
}