package com.exam.dal.repository;

import com.exam.dal.model.Score;
import com.exam.dal.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<Score> findBySubmission(Submission submission);
}
