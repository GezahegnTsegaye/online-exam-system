package com.exam.dal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an academic department in the institution
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToMany(mappedBy = "departments")
    @Builder.Default
    private Set<Teacher> teachers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "department_courses",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Builder.Default
    private Set<Course> courses = new HashSet<>();

    /**
     * Adds a course to the department
     * @param course The course to add
     */
    public void addCourse(Course course) {
        courses.add(course);
        if (course.getDepartments() != null) {
            course.getDepartments().add(this);
        }
    }

    /**
     * Removes a course from the department
     * @param course The course to remove
     */
    public void removeCourse(Course course) {
        courses.remove(course);
        if (course.getDepartments() != null) {
            course.getDepartments().remove(this);
        }
    }
}
