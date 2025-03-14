package com.exam.dal.repository;


import com.exam.dal.model.Option;
import com.exam.dal.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByQuestion(Question question);
}