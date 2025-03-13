package com.exam.dal.repository;


import com.exam.dal.entity.Answer;
import com.exam.dal.entity.Question;
import com.exam.dal.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findBySubmissionAndQuestion(Submission submission, Question question);
}