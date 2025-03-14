package com.exam.dal.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a teacher in the system who can create and manage courses and exams.
 * This entity extends the base User class with teacher-specific properties.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Teacher extends User {

    @Column(name = "department")
    private String department;

    @Column(name = "faculty_id", unique = true)
    private String facultyId;

    @Column(name = "title")
    private String title;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "office_location")
    private String officeLocation;

    @Column(name = "office_hours")
    private String officeHours;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Course> courses = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "teacher_departments",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    @Builder.Default
    private Set<Department> departments = new HashSet<>();

    /**
     * Adds a course to the teacher's list of courses and sets the teacher reference
     * @param course The course to add
     * @return The updated course
     */
    public Course addCourse(Course course) {
        courses.add(course);
        course.setTeacher(this);
        return course;
    }

    /**
     * Removes a course from the teacher's list of courses
     * @param course The course to remove
     */
    public void removeCourse(Course course) {
        courses.remove(course);
        course.setTeacher(null);
    }

    /**
     * Adds the teacher to a department
     * @param department The department to add the teacher to
     */
    public void addDepartment(Department department) {
        departments.add(department);
        department.getTeachers().add(this);
    }

    /**
     * Removes the teacher from a department
     * @param department The department to remove the teacher from
     */
    public void removeDepartment(Department department) {
        departments.remove(department);
        department.getTeachers().remove(this);
    }

    // Initialize inherited role to TEACHER
    @PrePersist
    public void prePersist() {
        if (getRole() == null) {
            setRole(Role.TEACHER);
        }
    }
}
