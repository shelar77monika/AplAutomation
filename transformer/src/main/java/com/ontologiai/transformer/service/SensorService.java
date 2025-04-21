package com.ontologiai.transformer.service;


import com.ontologiai.transformer.entities.Sensor;
import com.ontologiai.transformer.entities.SensorAPLMapping;
import com.ontologiai.transformer.repositories.SensorAPLMappingRepository;
import com.ontologiai.transformer.repositories.SensorRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class SensorService {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private SensorAPLMappingRepository mappingRepository;

    public void saveSensorsFromExcel(File file) throws IOException {
        InputStream inputStream =  new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);

        Sheet sensorSheet = workbook.getSheet("Sensors");
        Sheet mappingSheet = workbook.getSheet("Sensor_APL_Mapping");

        List<Sensor> sensors = new ArrayList<>();
        for (Row row : sensorSheet) {
            if (row.getRowNum() == 0) continue; // Skip header
            if(!"".equals(getCellValue(row.getCell(1)))){
                Sensor sensor = new Sensor(null,
                        getCellValue(row.getCell(1)),
                        getCellValue(row.getCell(2)),
                        (long) row.getCell(3).getNumericCellValue()
                );
                sensors.add(sensor);
            }
        }
        sensorRepository.deleteAll();
        sensorRepository.saveAll(sensors);

        List<SensorAPLMapping> mappings = new ArrayList<>();
        for (Row row : mappingSheet) {
            if (row.getRowNum() == 0) continue; // Skip header
            SensorAPLMapping mapping = new SensorAPLMapping();
            String aplMappingIdCellValue = getCellValue(row.getCell(0));
            if(!"".equalsIgnoreCase(aplMappingIdCellValue)){
                mapping.setAplMappingId((long) Double.parseDouble(aplMappingIdCellValue));
                mapping.setRevNr(Double.valueOf(getCellValue(row.getCell(1))));
                mapping.setNr((int) Double.parseDouble(getCellValue(row.getCell(2))));
                mapping.setOutstation(getCellValue(row.getCell(3)));
                mapping.setDeviceTag(getCellValue(row.getCell(4)));
                mapping.setFunction(getCellValue(row.getCell(5)));
                mapping.setDescription(getCellValue(row.getCell(6)));
                mapping.setTag(getCellValue(row.getCell(7)));
                mapping.setControllerTag(getCellValue(row.getCell(8)));
                mapping.setLowSetpointValue(getCellValue(row.getCell(9)));
                mapping.setHighSetpointValue(getCellValue(row.getCell(10)));
                mapping.setDelayTimer(getCellValue(row.getCell(11)));
                mapping.setHysteresis(getCellValue(row.getCell(12)));
                mapping.setControlLevel(getCellValue(row.getCell(13)));
                mapping.setElectronicSignatureType(getCellValue(row.getCell(14)));
                mapping.setUnit(getCellValue(row.getCell(15)));
                mapping.setSetting(getCellValue(row.getCell(16)));
                mapping.setControllerAlarmTag(getCellValue(row.getCell(17)));
                mapping.setAlarmType(getCellValue(row.getCell(18)));
                mapping.setReset(getCellValue(row.getCell(19)));
                mapping.setRemarks(getCellValue(row.getCell(20)));
                mappings.add(mapping);
            }
        }
        mappingRepository.deleteAll();
        mappingRepository.saveAll(mappings);
    }

    private String getCellValue(Cell sourceCell) {
        String cellValue = "";
        if(null != sourceCell){
            switch (sourceCell.getCellType()) {
                case STRING -> cellValue = sourceCell.getStringCellValue();
                case NUMERIC -> cellValue = String.valueOf(sourceCell.getNumericCellValue());
                case BOOLEAN -> cellValue = String.valueOf(sourceCell.getBooleanCellValue());
                case FORMULA -> cellValue = sourceCell.getCellFormula();
                default -> {
                    return "";
                }
            }
        }
        return cellValue;
    }
}
