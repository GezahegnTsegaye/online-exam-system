package com.exam.dal.model;

/**
 * Enum to represent submission status
 */
public enum SubmissionStatus {
    PENDING,        // Submission is pending
    IN_PROGRESS,    // Submission is being worked on
    COMPLETED,      // Submission is complete
    SUBMITTED,      // Submission has been submitted
    GRADED,         // Submission has been graded
    UNDER_REVIEW,   // Submission is being reviewed
    DISPUTED        // Submission has a dispute
}