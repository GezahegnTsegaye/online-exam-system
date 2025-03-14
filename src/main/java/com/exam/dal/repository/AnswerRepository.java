package com.exam.dal.repository;


import com.exam.dal.model.Answer;
import com.exam.dal.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    /**
     * Find answers by submission
     * @param submission The submission
     * @return List of answers for the submission
     */
    List<Answer> findBySubmission(Submission submission);

    /**
     * Find answers by question ID
     * @param questionId The question ID
     * @return List of answers for the question
     */
    List<Answer> findByQuestion_Id(Long questionId);
}