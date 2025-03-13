package com.exam.dal.repository;

import com.exam.dal.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.exam.dal.entity.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExam(Exam exam);
}
