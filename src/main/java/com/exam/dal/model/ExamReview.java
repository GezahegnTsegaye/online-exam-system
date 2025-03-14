package com.exam.dal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a review of an exam by an ExamReviewer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exam_reviews")
public class ExamReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private ExamReviewer reviewer;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    @Column(name = "content_rating", nullable = false)
    private Integer contentRating; // 1-5 scale

    @Column(name = "difficulty_rating", nullable = false)
    private Integer difficultyRating; // 1-5 scale

    @Column(name = "clarity_rating", nullable = false)
    private Integer clarityRating; // 1-5 scale

    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating; // 1-5 scale

    @Column(name = "comments", length = 2000)
    private String comments;

    @Column(name = "recommendation", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewRecommendation recommendation;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @Version
    private Long version;





    @PrePersist
    public void prePersist() {
        if (reviewDate == null) {
            reviewDate = LocalDateTime.now();
        }
        if (status == null) {
            status = ReviewStatus.PENDING;
        }
    }
}
