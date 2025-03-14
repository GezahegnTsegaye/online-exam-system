package com.exam.dal.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a student in the exam system
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {

    @Column(name = "student_number", unique = true)
    private String studentNumber;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "major")
    private String major;

    @Column(name = "department")
    private String department;

    @Column(name = "faculty")
    private String faculty;

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Builder.Default
    private Set<Course> enrolledCourses = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Exam> exams = new HashSet<>();

    /**
     * Enrolls the student in a course
     * @param course The course to enroll in
     */
    public void enrollCourse(Course course) {
        enrolledCourses.add(course);
        course.getStudents().add(this);
    }

    /**
     * Unenrolls the student from a course
     * @param course The course to unenroll from
     */
    public void unenrollCourse(Course course) {
        enrolledCourses.remove(course);
        course.getStudents().remove(this);
    }

    // Initialize inherited role to STUDENT
    @PrePersist
    public void prePersist() {
        if (getRole() == null) {
            setRole(Role.STUDENT);
        }
    }
}