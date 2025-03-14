package com.exam.dal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a detailed grading result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grade_results")
public class GradeResult implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", unique = true)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grading_configuration_id", nullable = false)
    private GradingConfiguration gradingConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_level_id")
    private GradeLevel gradeLevel;

    @Column(name = "total_score", nullable = false)
    private Double totalScore;

    @Column(name = "percentage_score")
    private Double percentageScore;

    @Column(name = "grade_point")
    private Double gradePoint;

    @Column(name = "passed")
    private Boolean passed;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * Calculates the grade result based on the grading configuration
     */
    public void calculateGrade() {
        if (submission == null || gradingConfiguration == null) {
            throw new IllegalStateException("Submission and Grading Configuration must be set");
        }

        // Calculate percentage score
        this.percentageScore = (totalScore / gradingConfiguration.getMaxScore()) * 100;

        // Determine grade level
        this.passed = this.percentageScore >= gradingConfiguration.getPassingScore();

        // Find the appropriate grade level
        findAndSetGradeLevel();

        // Set graded timestamp
        this.gradedAt = LocalDateTime.now();
    }

    /**
     * Finds and sets the appropriate grade level
     */
    private void findAndSetGradeLevel() {
        if (submission.getExam() == null) {
            return;
        }

        for (GradeLevel level : gradingConfiguration.getGradeLevels()) {
            if (totalScore >= level.getMinScore() && totalScore <= level.getMaxScore()) {
                this.gradeLevel = level;
                this.gradePoint = level.getGradePoint();
                this.remarks = level.getDescription();
                break;
            }
        }
    }
}