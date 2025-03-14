package com.exam.dal.repository;

import com.exam.dal.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import com.exam.dal.model.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExam(Exam exam);
}
