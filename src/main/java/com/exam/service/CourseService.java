package com.exam.service;

import com.exam.dal.dto.CourseRequest;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for handling course-related operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final SubmissionRepository submissionRepository;
    private final GradingConfigurationRepository gradingConfigurationRepository;
    private final GradeResultRepository gradeResultRepository;

    /**
     * Get the currently authenticated user
     *
     * @return The user entity
     * @throws RuntimeException if the user is not found or authentication is invalid
     */
    private User getCurrentUser() {
        // Get the current authentication context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or not authenticated
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {
            throw new UnauthorizedAccessException("No authenticated user found");
        }

        // Get the username (email)
        String email = authentication.getName();

        // Find and return the user
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Create a new course
     *
     * @param courseRequest The course data
     * @return The created course
     */
    public Course createCourse(CourseRequest courseRequest) {
        try {
            User currentUser = getCurrentUser();

            // Additional role validation
            if (currentUser.getRole() != Role.TEACHER && currentUser.getRole() != Role.ADMIN) {
                throw new UnauthorizedAccessException("Only teachers or admins can create courses");
            }

            Course course = Course.builder()
                    .title(courseRequest.getTitle())
                    .description(courseRequest.getDescription())
                    .teacher(currentUser)
                    .build();

            return courseRepository.save(course);
        } catch (Exception e) {
            // Log the error
            log.error("Error creating course", e);
            throw e;
        }
    }

    /**
     * Update an existing course
     *
     * @param id The course ID
     * @param courseRequest The updated course data
     * @return The updated course
     * @throws ResourceNotFoundException if the course is not found
     * @throws RuntimeException if the user doesn't have permission
     */
    public Course updateCourse(Long id, CourseRequest courseRequest) {
        try {
            Course course = getCourseById(id);
            User currentUser = getCurrentUser();

            // Robust permission check
            if (!course.getTeacher().getId().equals(currentUser.getId()) &&
                    !Role.ADMIN.equals(currentUser.getRole())) {
                throw new UnauthorizedAccessException("You don't have permission to update this course");
            }

            course.setTitle(courseRequest.getTitle());
            course.setDescription(courseRequest.getDescription());

            return courseRepository.save(course);
        } catch (Exception e) {
            log.error("Error updating course", e);
            throw e;
        }
    }
    /**
     * Get comprehensive course progress for a student
     *
     * @param courseId The course ID
     * @return Detailed progress information
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStudentCourseProgress(Long courseId) {
        Course course = getCourseById(courseId);
        User currentUser = getCurrentUser();

        // Ensure student is enrolled in the course
        if (!course.getStudents().contains(currentUser)) {
            throw new UnauthorizedAccessException("You are not enrolled in this course");
        }

        // Fetch all exams for the course
        List<Exam> exams = examRepository.findByCourse(course);

        // Prepare result map
        Map<String, Object> progressReport = new HashMap<>();
        progressReport.put("courseId", course.getId());
        progressReport.put("courseTitle", course.getTitle());

        // Calculate overall course progress
        List<Map<String, Object>> examProgressList = new ArrayList<>();
        int totalExams = exams.size();
        int completedExams = 0;
        double totalGradePoints = 0;
        int gradedExams = 0;

        // Process each exam
        for (Exam exam : exams) {
            Map<String, Object> examProgress = new HashMap<>();
            examProgress.put("examId", exam.getId());
            examProgress.put("examTitle", exam.getTitle());

            // Find submission for this exam
            Optional<Submission> submissionOpt = submissionRepository.findByStudentAndExam(currentUser, exam);

            if (submissionOpt.isPresent()) {
                Submission submission = submissionOpt.get();
                completedExams++;

                // Check for grade result
                Optional<GradeResult> gradeResultOpt = gradeResultRepository.findBySubmission(submission);

                if (gradeResultOpt.isPresent()) {
                    GradeResult gradeResult = gradeResultOpt.get();

                    examProgress.put("submitted", true);
                    examProgress.put("submissionTime", submission.getSubmittedAt());
                    examProgress.put("totalScore", gradeResult.getTotalScore());
                    examProgress.put("percentageScore", gradeResult.getPercentageScore());
                    examProgress.put("passed", gradeResult.getPassed());

                    if (gradeResult.getGradeLevel() != null) {
                        examProgress.put("grade", gradeResult.getGradeLevel().getGradeName());
                        examProgress.put("gradePoint", gradeResult.getGradePoint());

                        // Accumulate grade points
                        totalGradePoints += gradeResult.getGradePoint();
                        gradedExams++;
                    }
                } else {
                    examProgress.put("submitted", true);
                    examProgress.put("graded", false);
                }
            } else {
                examProgress.put("submitted", false);
            }

            examProgressList.add(examProgress);
        }

        // Calculate overall progress metrics
        double completionRate = totalExams > 0 ? (double) completedExams / totalExams * 100 : 0;
        double averageGradePoint = gradedExams > 0 ? totalGradePoints / gradedExams : 0;

        // Populate progress report
        Map<String, Object> progressMetrics = new HashMap<>();
        progressMetrics.put("totalExams", totalExams);
        progressMetrics.put("completedExams", completedExams);
        progressMetrics.put("completionRate", completionRate);
        progressMetrics.put("averageGradePoint", averageGradePoint);

        progressReport.put("exams", examProgressList);
        progressReport.put("progress", progressMetrics);

        return progressReport;
    }

    /**
     * Get course students with their detailed progress (for teachers)
     *
     * @param courseId The course ID
     * @return List of student progress details
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCourseStudentsWithProgress(Long courseId) {
        Course course = getCourseById(courseId);
        User currentUser = getCurrentUser();

        // Verify teacher or admin access
        if (!course.getTeacher().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("You don't have permission to view students for this course");
        }

        // Fetch all exams for the course
        List<Exam> exams = examRepository.findByCourse(course);

        // Prepare student progress list
        return course.getStudents().stream()
                .map(student -> {
                    Map<String, Object> studentProgress = new HashMap<>();

                    // Student basic info
                    Map<String, Object> studentInfo = new HashMap<>();
                    studentInfo.put("id", student.getId());
                    studentInfo.put("name", student.getName());
                    studentInfo.put("email", student.getEmail());

                    // Calculate progress
                    int totalExams = exams.size();
                    int completedExams = 0;
                    double totalGradePoints = 0;
                    int gradedExams = 0;

                    // Process each exam
                    List<Map<String, Object>> examDetails = new ArrayList<>();
                    for (Exam exam : exams) {
                        Map<String, Object> examDetail = new HashMap<>();
                        examDetail.put("examId", exam.getId());
                        examDetail.put("examTitle", exam.getTitle());

                        Optional<Submission> submissionOpt = submissionRepository.findByStudentAndExam(student, exam);

                        if (submissionOpt.isPresent()) {
                            Submission submission = submissionOpt.get();
                            completedExams++;

                            Optional<GradeResult> gradeResultOpt = gradeResultRepository.findBySubmission(submission);

                            if (gradeResultOpt.isPresent()) {
                                GradeResult gradeResult = gradeResultOpt.get();

                                examDetail.put("submitted", true);
                                examDetail.put("totalScore", gradeResult.getTotalScore());
                                examDetail.put("percentageScore", gradeResult.getPercentageScore());
                                examDetail.put("passed", gradeResult.getPassed());

                                if (gradeResult.getGradeLevel() != null) {
                                    examDetail.put("grade", gradeResult.getGradeLevel().getGradeName());
                                    examDetail.put("gradePoint", gradeResult.getGradePoint());

                                    totalGradePoints += gradeResult.getGradePoint();
                                    gradedExams++;
                                }
                            } else {
                                examDetail.put("submitted", true);
                                examDetail.put("graded", false);
                            }
                        } else {
                            examDetail.put("submitted", false);
                        }

                        examDetails.add(examDetail);
                    }

                    // Calculate progress metrics
                    Map<String, Object> progressInfo = new HashMap<>();
                    progressInfo.put("totalExams", totalExams);
                    progressInfo.put("completedExams", completedExams);
                    progressInfo.put("completionRate", totalExams > 0 ? (double) completedExams / totalExams * 100 : 0);
                    progressInfo.put("averageGradePoint", gradedExams > 0 ? totalGradePoints / gradedExams : 0);

                    studentProgress.put("student", studentInfo);
                    studentProgress.put("progress", progressInfo);
                    studentProgress.put("exams", examDetails);

                    return studentProgress;
                })
                .collect(Collectors.toList());
    }


    /**
     * Get all courses in the system
     *
     * @return List of all courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Get a course by its ID
     *
     * @param id The course ID
     * @return The course entity
     * @throws ResourceNotFoundException if the course is not found
     */
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }



    /**
     * Delete a course
     *
     * @param id The course ID
     * @throws ResourceNotFoundException if the course is not found
     * @throws RuntimeException if the user doesn't have permission
     */
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        User currentUser = getCurrentUser();

        if (!course.getTeacher().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You don't have permission to delete this course");
        }

        courseRepository.delete(course);
    }

    /**
     * Enroll a student in a course
     *
     * @param courseId The course ID
     * @param studentId The student user ID
     * @return The updated course
     * @throws ResourceNotFoundException if the course or student is not found
     * @throws RuntimeException if the user is not a student
     */
    public Course enrollStudent(Long courseId, Long studentId) {
        Course course = getCourseById(courseId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not a student");
        }

        course.getStudents().add(student);
        return courseRepository.save(course);
    }

    /**
     * Unenroll a student from a course
     *
     * @param courseId The course ID
     * @param studentId The student user ID
     * @return The updated course
     * @throws ResourceNotFoundException if the course or student is not found
     */
    public Course unenrollStudent(Long courseId, Long studentId) {
        Course course = getCourseById(courseId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        course.getStudents().remove(student);
        return courseRepository.save(course);
    }

    /**
     * Get all courses taught by the current teacher
     *
     * @return List of courses
     */
    public List<Course> getTeacherCourses() {
        User currentUser = getCurrentUser();
        return courseRepository.findByTeacher(currentUser);
    }

    /**
     * Get all courses in which the current student is enrolled
     *
     * @return List of courses
     */
    public List<Course> getStudentCourses() {
        User currentUser = getCurrentUser();
        return courseRepository.findByStudentsContaining(currentUser);
    }

    /**
     * Check if a student is already enrolled in a course
     *
     * @param courseId The course ID
     * @param studentId The student ID
     * @return true if enrolled, false otherwise
     */
    public boolean isStudentEnrolled(Long courseId, Long studentId) {
        Course course = getCourseById(courseId);
        return course.getStudents().stream()
                .anyMatch(student -> student.getId().equals(studentId));
    }

}