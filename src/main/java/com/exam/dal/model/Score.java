package com.exam.dal.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scores", schema = "public")
public class Score implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long scoreId;

	@Column(name = "total_score", nullable = false)
	private Double totalScore;

	@Column(name = "percentage_score")
	private Double percentageScore;

	@Column(name = "reading", length = 50)
	private String reading;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 20)
	private ScoreStatus status;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "submission_id", unique = true)
	private Submission submission;

	@Column(name = "graded_at")
	private LocalDateTime gradedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "graded_by")
	private User gradedBy;

	/**
	 * Enum to represent score status
	 */
	public enum ScoreStatus {
		PASS,           // Student passed the exam
		FAIL,           // Student failed the exam
		INCOMPLETE,     // Submission is not fully graded
		PENDING,        // Score is pending review
		DISPUTED        // Score is under dispute
	}

	/**
	 * Calculates percentage score based on total score and exam's total marks
	 */
	public void calculatePercentageScore() {
		if (submission != null && submission.getExam() != null) {
			Double totalExamMarks = submission.getExam().getTotalMarks();

			if (totalExamMarks != null && totalExamMarks > 0) {
				this.percentageScore = (this.totalScore / totalExamMarks) * 100;

				// Determine reading and status based on percentage
				determineGradeDetails();
			}
		}
	}

	/**
	 * Determines grade details based on percentage score
	 */
	private void determineGradeDetails() {
		if (percentageScore >= 90) {
			this.reading = "Excellent";
			this.status = ScoreStatus.PASS;
		} else if (percentageScore >= 80) {
			this.reading = "Very Good";
			this.status = ScoreStatus.PASS;
		} else if (percentageScore >= 70) {
			this.reading = "Good";
			this.status = ScoreStatus.PASS;
		} else if (percentageScore >= 60) {
			this.reading = "Satisfactory";
			this.status = ScoreStatus.PASS;
		} else if (percentageScore >= 50) {
			this.reading = "Marginal";
			this.status = ScoreStatus.FAIL;
		} else {
			this.reading = "Unsatisfactory";
			this.status = ScoreStatus.FAIL;
		}
	}

	/**
	 * Creates a score for a submission
	 * @param submission The submission to create score for
	 * @param totalScore The total score obtained
	 * @param gradedBy The user who graded the submission
	 * @return The created score
	 */
	public static Score createScore(Submission submission, Double totalScore, User gradedBy) {
		Score score = new Score();
		score.setSubmission(submission);
		score.setTotalScore(totalScore);
		score.setGradedBy(gradedBy);
		score.setGradedAt(LocalDateTime.now());
		score.calculatePercentageScore();

		return score;
	}

	/**
	 * Prepares the score before persisting
	 */
	@PrePersist
	public void prePersist() {
		if (gradedAt == null) {
			gradedAt = LocalDateTime.now();
		}

		if (status == null) {
			status = ScoreStatus.PENDING;
		}

		calculatePercentageScore();
	}

	/**
	 * Prepares the score before updating
	 */
	@PreUpdate
	public void preUpdate() {
		calculatePercentageScore();
	}

	/**
	 * Updates the score
	 * @param totalScore New total score
	 * @param gradedBy User who is updating the score
	 */
	public void updateScore(Double totalScore, User gradedBy) {
		this.totalScore = totalScore;
		this.gradedBy = gradedBy;
		this.gradedAt = LocalDateTime.now();
		calculatePercentageScore();
	}
}