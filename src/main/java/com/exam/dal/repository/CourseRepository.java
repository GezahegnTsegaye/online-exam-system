package com.exam.dal.repository;

import com.exam.dal.model.Course;
import com.exam.dal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacher(User teacher);
    List<Course> findByStudentsContaining(User student);
}