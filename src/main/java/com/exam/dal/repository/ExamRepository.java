package com.exam.dal.repository;

import com.exam.dal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import com.exam.dal.model.Exam;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByCourse(Course course);
    List<Exam> findByCourseAndPublishedTrue(Course course);
}