package com.ontologiai.transformer.repositories;


import com.ontologiai.transformer.entities.SensorAPLMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorAPLMappingRepository extends JpaRepository<SensorAPLMapping, Long> {
    List<SensorAPLMapping> findByAplMappingId(Long aplMappingId);
}