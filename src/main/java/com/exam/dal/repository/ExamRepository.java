package com.exam.dal.repository;

import com.exam.dal.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.exam.dal.entity.Exam;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByCourse(Course course);
    List<Exam> findByCourseAndPublishedTrue(Course course);
}