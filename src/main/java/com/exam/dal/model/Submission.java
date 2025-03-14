package com.exam.dal.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "is_completed")
    private boolean completed;

    @Column(name = "is_graded")
    private boolean graded;

    @Column(name = "total_score")
    private Double totalScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @OneToMany(mappedBy = "submission",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Answer> answers = new HashSet<>();

    @OneToOne(mappedBy = "submission",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Score score;

    /**
     * Enum to represent submission status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubmissionStatus status;

    /**
     * Adds an answer to the submission
     * @param answer The answer to add
     */
    public void addAnswer(Answer answer) {
        answers.add(answer);
        answer.setSubmission(this);
    }

    /**
     * Removes an answer from the submission
     * @param answer The answer to remove
     */
    public void removeAnswer(Answer answer) {
        answers.remove(answer);
        answer.setSubmission(null);
    }

    /**
     * Sets the score for this submission
     * @param score The score to set
     */
    public void setScore(Score score) {
        if (score == null) {
            if (this.score != null) {
                this.score.setSubmission(null);
            }
        } else {
            score.setSubmission(this);
        }
        this.score = score;
    }

    /**
     * Grade the submission
     * @param totalScore The total score for the submission
     */
    public void grade(Double totalScore) {
        this.totalScore = totalScore;
        this.graded = true;
        this.status = SubmissionStatus.GRADED;
    }

    /**
     * Reset the grading of the submission
     */
    public void resetGrading() {
        this.totalScore = null;
        this.graded = false;
        this.status = SubmissionStatus.PENDING;
    }

    /**
     * Prepares the submission before persisting
     */
    @PrePersist
    public void prePersist() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }

        // Default status if not set
        if (status == null) {
            status = SubmissionStatus.PENDING;
        }

        // Default graded status
        if (!graded) {
            graded = false;
        }
    }


}