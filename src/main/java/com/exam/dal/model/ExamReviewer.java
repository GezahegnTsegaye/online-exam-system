package com.exam.dal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an exam reviewer in the system who can review and provide feedback on exams
 * This entity extends the base User class with reviewer-specific properties
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exam_reviewers")
@PrimaryKeyJoinColumn(name = "user_id")
public class ExamReviewer extends User {

    @Column(name = "reviewer_id", unique = true)
    private String reviewerId;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "is_external", nullable = false)
    private boolean external;

    @ManyToMany
    @JoinTable(
            name = "reviewer_departments",
            joinColumns = @JoinColumn(name = "reviewer_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    @Builder.Default
    private Set<Department> departments = new HashSet<>();

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ExamReview> reviews = new HashSet<>();

    /**
     * Adds the reviewer to a department
     * @param department The department to add the reviewer to
     */
    public void addDepartment(Department department) {
        departments.add(department);
    }

    /**
     * Removes the reviewer from a department
     * @param department The department to remove the reviewer from
     */
    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    /**
     * Adds a review to the reviewer's list of reviews and sets the reviewer reference
     * @param review The review to add
     * @return The updated review
     */
    public ExamReview addReview(ExamReview review) {
        reviews.add(review);
        review.setReviewer(this);
        return review;
    }

    /**
     * Removes a review from the reviewer's list of reviews
     * @param review The review to remove
     */
    public void removeReview(ExamReview review) {
        reviews.remove(review);
        review.setReviewer(null);
    }

    // Initialize inherited role to custom reviewer role
    @PrePersist
    public void prePersist() {
        if (getRole() == null) {
            setRole(Role.REVIEWER);
        }
    }
}