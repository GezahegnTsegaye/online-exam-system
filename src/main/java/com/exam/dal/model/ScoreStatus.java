package com.exam.dal.model;

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
