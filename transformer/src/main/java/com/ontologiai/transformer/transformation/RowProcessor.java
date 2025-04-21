package com.ontologiai.transformer.transformation;

import com.ontologiai.transformer.entities.Sensor;
import com.ontologiai.transformer.entities.SensorAPLMapping;
import com.ontologiai.transformer.model.RowInformation;
import com.ontologiai.transformer.repositories.SensorAPLMappingRepository;
import com.ontologiai.transformer.repositories.SensorRepository;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RowProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RowProcessor.class);

    @Autowired
    SensorRepository sensorRepository;

    @Autowired
    SensorAPLMappingRepository sensorAPLMappingRepository;


    public void processRow(Sheet newSheet, Workbook refWorkbook, RowInformation rowInformation) {
        Sensor sensor = sensorRepository.findBySensorNameAndIoType(DeviceTagUtility.getDeviceType(rowInformation.getDeviceTag()), rowInformation.getIoType());
        if (null != sensor) {
            logger.info(sensor.toString());
            List<SensorAPLMapping> sensorAPLMappingList = sensorAPLMappingRepository.findByAplMappingId(sensor.getAplMappingId());
            logger.info(sensorAPLMappingList.toString());
            addRowToSheet(newSheet, sensorAPLMappingList, rowInformation);

        }
    }

    private static void addRowToSheet(Sheet newSheet, List<SensorAPLMapping> sensorAPLMappingList, RowInformation rowInformation) {
        // Start writing at the next available row
        int rowNum = newSheet.getLastRowNum() + 1;

        for (SensorAPLMapping mapping : sensorAPLMappingList) {
            Row row = newSheet.createRow(rowNum++);

            // Filling columns as per the Excel sequence you shared
            row.createCell(0).setCellValue(mapping.getRevNr() != null ? mapping.getRevNr() : 1);  // Rev Nr
            row.createCell(1).setCellValue(rowNum-1);                                               // Nr
            row.createCell(2).setCellValue(rowInformation.getOutstation());                       // Outstation
            row.createCell(3).setCellValue(rowInformation.getDeviceTag());                       // Device Tag
            row.createCell(4).setCellValue(mapping.getFunction());                               // Function
            row.createCell(5).setCellValue(getDescription(rowInformation, mapping));     // Description
            row.createCell(6).setCellValue(getTag(mapping));                                    // Tag
            row.createCell(7).setCellValue(getControllerTag(mapping));                          // Controller Tag
            row.createCell(8).setCellValue(getLowSetpointValue(mapping, rowInformation));                       // Range (Low) / State 0
            row.createCell(9).setCellValue(getHighSetpointValue(mapping, rowInformation));                      // Range (High) / State 1
//            row.createCell(10).setCellValue("");                                                 // State 2
//            row.createCell(11).setCellValue("");                                                 // State 3
//            row.createCell(12).setCellValue("");                                                 // State 4
//            row.createCell(13).setCellValue("");                                                 // State 5
//            row.createCell(14).setCellValue("");                                                 // State 6
//            row.createCell(15).setCellValue("");                                                 // State 7
//            row.createCell(16).setCellValue("");                                                 // State 8
//            row.createCell(17).setCellValue("");                                                 // State 9
//            row.createCell(18).setCellValue("");                                                 // State 16
//            row.createCell(19).setCellValue("");                                                 // State 32
//            row.createCell(20).setCellValue("");                                                 // State 64
//            row.createCell(21).setCellValue("");                                                 // State 128
//            row.createCell(22).setCellValue("");                                                 // State 8192
//            row.createCell(23).setCellValue("");                                                 // State 16384
//            row.createCell(24).setCellValue("");                                                 // State 32768
//            row.createCell(25).setCellValue(mapping.getDelayTimer());                            // Delay Timer (Sec)
//            row.createCell(26).setCellValue(mapping.getHysteresis());                            // Hysteresis
//            row.createCell(27).setCellValue(mapping.getControlLevel());                          // Control Level
//            row.createCell(28).setCellValue(mapping.getElectronicSignatureType());               // Electronic Signature Type
//            row.createCell(29).setCellValue(mapping.getUnit());                                  // Unit
//            row.createCell(30).setCellValue(mapping.getSetting());                               // Setting
//            row.createCell(31).setCellValue(getControllerAlarmTag(mapping));                    // Controller Alarm Tag
//            row.createCell(32).setCellValue(mapping.getAlarmType());                             // Alarm Type
//            row.createCell(33).setCellValue(mapping.getReset());                                 // Reset
//            row.createCell(34).setCellValue(mapping.getRemarks());                               // Remarks

            row.createCell(10).setCellValue(mapping.getDelayTimer());                            // Delay Timer (Sec)
            row.createCell(11).setCellValue(mapping.getHysteresis());                            // Hysteresis
            row.createCell(12).setCellValue(mapping.getControlLevel());                          // Control Level
            row.createCell(13).setCellValue(mapping.getElectronicSignatureType());               // Electronic Signature Type
            row.createCell(14).setCellValue(mapping.getUnit());                                  // Unit
            row.createCell(15).setCellValue(mapping.getSetting());                               // Setting
            row.createCell(16).setCellValue(getControllerAlarmTag(mapping));                    // Controller Alarm Tag
            row.createCell(17).setCellValue(mapping.getAlarmType());                             // Alarm Type
            row.createCell(18).setCellValue(mapping.getReset());                                 // Reset
            row.createCell(19).setCellValue(mapping.getRemarks());                               // Remarks
        }
    }

    private static String getDescription(RowInformation rowInformation, SensorAPLMapping mapping) {
        return  mapping.getDescription().replace("{X}",rowInformation.getPointDescriptor() );
    }

    private static String getControllerAlarmTag(SensorAPLMapping mapping) {

        String result = mapping.getDeviceTag().replaceFirst("^[^-]+", "");
        if("-".equals(mapping.getControllerTag())){
            return "-";
        }
        return mapping.getControllerAlarmTag().replace("-{DT}", result);
    }

    private static String getHighSetpointValue(SensorAPLMapping mapping, RowInformation rowInformation) {
        if(mapping.getControllerTag().contains("P") || mapping.getControllerTag().contains("E") || mapping.getControllerTag().contains("O")){
            return rowInformation.getRangeHigh();
        }
        return mapping.getHighSetpointValue();
    }

    private static String getLowSetpointValue(SensorAPLMapping mapping, RowInformation rowInformation) {

        if(mapping.getControllerTag().contains("P") || mapping.getControllerTag().contains("E") || mapping.getControllerTag().contains("O")){
            return rowInformation.getRangeLow();
        }
        return mapping.getLowSetpointValue();
    }

    private static String getControllerTag(SensorAPLMapping mapping) {

        String result = mapping.getDeviceTag().replaceFirst("^[^-]+", "");
        if("-".equals(mapping.getControllerTag())){
            return "-";
        }
        return mapping.getControllerTag().replace("-{Z}", result);
    }

    private static String getTag(SensorAPLMapping mapping) {
        return mapping.getTag().replace("{Y}",mapping.getDeviceTag());
    }

}
