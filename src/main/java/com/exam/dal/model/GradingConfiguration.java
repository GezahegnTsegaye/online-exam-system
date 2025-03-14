package com.exam.dal.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a comprehensive grading configuration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grading_configurations")
public class GradingConfiguration implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "passing_score", nullable = false)
    private Double passingScore;

    @Column(name = "max_score", nullable = false)
    private Double maxScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "grading_type")
    private GradingType gradingType;

    @OneToMany(mappedBy = "gradingConfiguration",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private Set<GradeLevel> gradeLevels = new HashSet<>();

    /**
     * Adds a grade level to the configuration
     * @param gradeLevel The grade level to add
     */
    public void addGradeLevel(GradeLevel gradeLevel) {
        gradeLevels.add(gradeLevel);
        gradeLevel.setGradingConfiguration(this);
    }

    /**
     * Removes a grade level from the configuration
     * @param gradeLevel The grade level to remove
     */
    public void removeGradeLevel(GradeLevel gradeLevel) {
        gradeLevels.remove(gradeLevel);
        gradeLevel.setGradingConfiguration(null);
    }

}