package com.ontologiai.transformer.repositories;

import com.ontologiai.transformer.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Sensor findBySensorNameAndIoType(String sensorName, String ioType);

}

