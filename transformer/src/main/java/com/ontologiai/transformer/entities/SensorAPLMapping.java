package com.ontologiai.transformer.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity for Sensor APL Mapping
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorAPLMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long aplMappingId;
    private Double RevNr;
    private int Nr;
    private String outstation;
    private String deviceTag;
    private String function;
    private String description;
    private String tag;
    private String controllerTag;
    private String lowSetpointValue;
    private String highSetpointValue;
    private String delayTimer;
    private String hysteresis;
    private String controlLevel;
    private String electronicSignatureType;
    private String unit;
    private String setting;
    private String controllerAlarmTag;
    private String alarmType;
    private String reset;
    private String remarks;

}
