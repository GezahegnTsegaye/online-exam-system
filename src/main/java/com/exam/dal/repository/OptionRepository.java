package com.exam.dal.repository;


import com.exam.dal.entity.Option;
import com.exam.dal.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByQuestion(Question question);
}