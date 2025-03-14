package com.exam.dal.repository;

import com.exam.dal.model.GradingConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingConfigurationRepository extends JpaRepository<GradingConfiguration, Long> {
    // Custom query methods as needed
}
