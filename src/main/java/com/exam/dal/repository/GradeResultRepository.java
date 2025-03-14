package com.exam.dal.repository;

import com.exam.dal.model.GradeResult;
import com.exam.dal.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeResultRepository extends JpaRepository<GradeResult, Long> {
    Optional<GradeResult> findBySubmission(Submission submission);
}
