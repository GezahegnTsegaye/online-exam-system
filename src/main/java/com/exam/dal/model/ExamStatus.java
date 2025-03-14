package com.exam.dal.model;

/**
 * Enum to represent exam status
 */
public enum ExamStatus {
    DRAFT,       // Exam is being created
    SCHEDULED,   // Exam is scheduled but not yet started
    ACTIVE,      // Exam is currently ongoing
    COMPLETED,   // Exam has ended
    REVIEWED,    // Exam has been graded and reviewed
    CANCELLED    // Exam was cancelled
}