package com.exam.dal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Represents a specific grade in a grading configuration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grade_levels")
public class GradeLevel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grading_configuration_id", nullable = false)
    private GradingConfiguration gradingConfiguration;

    @Column(name = "grade_name", nullable = false)
    private String gradeName;

    @Column(name = "min_score", nullable = false)
    private Double minScore;

    @Column(name = "max_score", nullable = false)
    private Double maxScore;

    @Column(name = "grade_point")
    private Double gradePoint;

    @Column(name = "description")
    private String description;
}