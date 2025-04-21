package com.ontologiai.transformer.transformation;

import com.ontologiai.transformer.TransformerApplication;
import com.ontologiai.transformer.config.ConfigurationReader;
import com.ontologiai.transformer.config.TransformerConfiguration;
import com.ontologiai.transformer.model.RowInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;


@Component
public class ExcelProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ExcelProcessor.class);

//    private static final String[] HEADERS = {
//            "Rev Nr", "Nr", "Outstation", "Device Tag", "Function", "Description", "Tag", "Controller Tag",
//            "Low Setpoint Value / Descriptor State 0", "High Setpoint Value / Descriptor State 1", "State 2", "State 3", "State 4", "State 5", "State 6",
//            "State 7", "State 8", "State 9", "State 16", "State 32", "State 64", "State 128", "State 8192",
//            "State 16384", "State 32768", "Delay Timer (Sec)", "Hysteresis", "Control Level",
//            "Electronic Signature Type", "Unit", "Setting", "Controller Alarm Tag", "Alarm Type",
//            "Reset", "Remarks"
//    };

    private static final String[] HEADERS = {
            "Rev Nr", "Nr", "Outstation", "Device Tag", "Function", "Description", "Tag", "Controller Tag",
            "Low Setpoint Value / Descriptor State 0", "High Setpoint Value / Descriptor State 1", "Delay Timer (Sec)",
            "Hysteresis", "Control Level","Electronic Signature Type", "Unit", "Setting", "Controller Alarm Tag",
            "Alarm Type", "Reset", "Remarks"
    };


    private int deviceTagColumnIndex = -1;
    private int pointDescriptorColumnIndex = -1;
    private int deviceTypeColumnIndex = -1;


    @Autowired
    ConfigurationReader configurationReader;

    @Autowired
    RowProcessor rowProcessor;

    @Autowired
    DeviceTagMapper deviceTagMapper;


    public void readAndWriteExcelFile(File inputFile, File outputFile) throws IOException {
        logger.info("Reading Excel file: {}", inputFile.getName());

        try (Workbook workbook = WorkbookFactory.createWorkbook(inputFile);
             Workbook newWorkbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(outputFile);
             InputStream refFis = getReferenceFileStream();
             Workbook refWorkbook = new XSSFWorkbook(refFis)) {

            copyReferenceSheets(refWorkbook, newWorkbook);
            processFloormanagerSheet(workbook, newWorkbook, refWorkbook);
            newWorkbook.write(fos);

        } catch (Exception e) {
            logger.error("Error processing Excel file", e);
            throw e;
        }
    }

    static class WorkbookFactory {
        static Workbook createWorkbook(File file) throws IOException {
            try (FileInputStream fis = new FileInputStream(file)) {
                return file.getName().endsWith(".xlsx") ? new XSSFWorkbook(fis) : new HSSFWorkbook(fis);
            }
        }
    }

    private static Sheet getOrCreateSheet(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        return (sheet != null) ? sheet : workbook.createSheet(sheetName);
    }

    private  void processFloormanagerSheet(Workbook workbook, Workbook newWorkbook, Workbook refWorkbook) {
        Sheet sheet = workbook.getSheet("Floormanager");
        if (sheet == null) {
            sheet = workbook.getSheet("L270-01-1PNL-001");
            if(sheet == null ){
                logger.warn("Sheet 'Floormanager' not found.");
                return;
            }
        }

        Sheet newSheet = getOrCreateSheet(newWorkbook, "J270-06-demo");
        addHeaderRow(newSheet);

        deviceTagColumnIndex = getColumnIndex(sheet, "Device Tag");
        pointDescriptorColumnIndex = getColumnIndex(sheet, "Point Descriptor");
        deviceTypeColumnIndex = getColumnIndex(sheet, "IO Type");


        if(deviceTagColumnIndex != -1 && pointDescriptorColumnIndex != -1 && deviceTypeColumnIndex != -1){
            for (Row row : sheet) {
                if (row.getRowNum() == 1 || row.getRowNum() == 0) continue; // Skip header row
                logger.info("Processing row number {}", row.getRowNum());
                processRow(row, newSheet, refWorkbook, deviceTagColumnIndex, pointDescriptorColumnIndex, deviceTypeColumnIndex);
            }
        }else{
            logger.info("Excel can not be processed cause Device Tag and Point Descriptor columns not found in the excel");
        }

    }

    private  void processRow(Row row, Sheet newSheet, Workbook refWorkbook, int deviceTagColumnIndex, int pointDescriptorColumnIndex, int deviceTypeColumnIndex) {
        if (isRowEmpty(row)) {
            logger.debug("Skipping empty row");
            return;
        }

        if("Rev".equals(getCellValue(row, 0))){
            return;
        }

//        if (row.getZeroHeight()) {
//            logger.info("Skipping hidden row {}", row.getRowNum());
//            return;
//        }

        RowInformation rowInformation = buildRowInformation(row);
        if(null != rowInformation){
            logger.info("Parsed RowInformation: {}", rowInformation);
            logger.debug("Processing row - Device Tag: {}, Point Description: {}, Device Type: {}",
                    rowInformation.getDeviceTag(), rowInformation.getDescription(), rowInformation.getIoType());
            rowProcessor.processRow(newSheet, refWorkbook, rowInformation);

        }

//        if (standardDeviceTag != null && !standardDeviceTag.equalsIgnoreCase("Device Tag") && !standardDeviceTag.isEmpty()) {
//            copyRowsFromRefSheet(newSheet, refWorkbook, standardDeviceTag, deviceTag, pointDescription, deviceType);
//        }
    }

    public boolean isNotNumber(String str) {
        return !str.matches("-?\\d+(\\.\\d+)?"); // Matches integers and decimals
    }

    private RowInformation buildRowInformation(Row row) {
        RowInformation rowInformation=null;
        try{
            rowInformation = RowInformation.builder()
                    .rev(getCellValue(row, 0))
                    .nr(Double.parseDouble(getCellValue(row, 1)))
                    .outstation(getCellValue(row, 2))
                    .deviceTag(getCellValue(row, 3))
                    .pointDescriptor(getCellValue(row, 4))
                    .pidNr(getCellValue(row, 5))
                    .ioType(getCellValue(row, 6))
                    .description(getCellValue(row, 7))
                    .power(getCellValue(row, 8))
                    .signalType(getCellValue(row, 9))
                    .converter(getCellValue(row, 10))
                    .splitter(getCellValue(row, 11))
                    .rangeLow(getCellValue(row, 12))
                    .rangeHigh(getCellValue(row, 13))
                    .unit(getCellValue(row, 14))
                    .accuracy(getCellValue(row, 15))
                    .mfgr(getCellValue(row, 16))
                    .model(getCellValue(row, 17))
                    .calCertReq(getCellValue(row, 18))
                    .techDatasheetNr(getCellValue(row, 19))
                    .signalTypeHc900(getCellValue(row, 20))
                    .rangeLowHc900(getCellValue(row, 21))
                    .rangeHighHc900(getCellValue(row, 22))
                    .unitHc900(getCellValue(row, 23))
                    .remarkHc900(getCellValue(row, 24))
                    .ebiPointDescriptor(getCellValue(row, 25))
                    .ebiTag(getCellValue(row, 26))
                    .hcdTag(getCellValue(row, 27))
                    .remarks(getCellValue(row, 28))
                    .roomNumber(getCellValue(row, 29))
                    .slotNo(getCellValue(row, 30))
                    .moduleNo(getCellValue(row, 31))
                    .channelNo(getCellValue(row, 32))
                    .build();
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }

        return rowInformation;
    }

    private int getColumnIndex(Sheet sheet, String columnName) {
        TransformerConfiguration configuration = configurationReader.getConfiguration();
        Row headerRow = getHeaderRow(sheet, configuration.getRequiredHeaders());
        if(null != headerRow){
            for (Cell cell : headerRow) {
                String cellValue = cell.getStringCellValue().replace("\n", "").trim();
                if (cellValue.equalsIgnoreCase(columnName)) {
                    return cell.getColumnIndex();
                }
            }
        }
        return -1;
    }


    private Row getHeaderRow(Sheet sheet, List<String> requiredHeaders) {
        Row headerRow = sheet.getRow(0);

        if (isRowEmpty(headerRow) || containsRequiredHeaders(headerRow, requiredHeaders)) {
            logger.debug("Skipping header row at index {} cause it's empty or missing required headers", 0);
            logger.debug("Trying to find headers at row index {}", 1);

            headerRow = sheet.getRow(1);
            if (isRowEmpty(headerRow) || containsRequiredHeaders(headerRow, requiredHeaders)) {
                logger.error("Did not find the required headers in the Excel file");
                return null;
            }
        }
        return headerRow;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean containsRequiredHeaders(Row row, List<String> requiredHeaders) {
        Set<String> headersInRow = new HashSet<>();
        for (Cell cell : row) {
            headersInRow.add(cell.toString().trim());
        }
        return !headersInRow.containsAll(requiredHeaders);
    }


    private void copyRowsFromRefSheet(Sheet newSheet, Workbook refWorkbook, String standardDeviceTag, String deviceTag, String pointDescription, String deviceType) {
        try {
            List<Row> matchedRows = getMatchingRows(refWorkbook, standardDeviceTag, deviceType);
            copyRows(newSheet, matchedRows, standardDeviceTag, deviceTag, pointDescription);
        } catch (Exception e) {
            logger.error("Error processing reference workbook", e);
        }
    }

    private  List<Row> getMatchingRows(Workbook refWorkbook, String standardDeviceTag, String deviceType) {
        List<Row> matchingRows = new ArrayList<>();
        Sheet sheet = refWorkbook.getSheetAt(1);
        //int deviceTagColumnIndex = getColumnIndex(sheet, "Device Tag");
        if (deviceTagColumnIndex == -1) return matchingRows;

        for (Row row : sheet) {
            String deviceTag = getCellValue(row, deviceTagColumnIndex);
            String rowDeviceType = getCellValue(row, deviceTypeColumnIndex);
            if (deviceTag.equalsIgnoreCase(standardDeviceTag) && rowDeviceType.equalsIgnoreCase(deviceType)) {
                matchingRows.add(row);
            }
        }
        return matchingRows;
    }

    private void copyRows(Sheet newSheet, List<Row> sourceRows, String standardDeviceTag, String deviceTag, String pointDescription) {
        int newRowNum = newSheet.getLastRowNum() + 1;
        for (Row sourceRow : sourceRows) {
            Row newRow = newSheet.createRow(newRowNum++);
            copyRowData(sourceRow, newRow, standardDeviceTag, deviceTag, pointDescription);
        }
    }

    private  void copyRowData(Row sourceRow, Row newRow, String standardDeviceTag, String deviceTag, String pointDescription) {
        TransformerConfiguration configuration = configurationReader.getConfiguration();
        for (Cell sourceCell : sourceRow) {
            Cell newCell = newRow.createCell(sourceCell.getColumnIndex());

            switch (sourceCell.getCellType()) {
                case STRING:
                    if(sourceCell.getColumnIndex() == configuration.getDeviceTagIndexInOutputFile()){
                        newCell.setCellValue(sourceCell.getStringCellValue().replace(standardDeviceTag, deviceTag));
                    }else if(sourceCell.getColumnIndex() == configuration.getPointDescriptionIndexInOutputFile()){
                        String regexPointDescriptor = getRegexPointDescriptor(pointDescription, deviceTag);
                        String replacedString = replaceFirstPart(regexPointDescriptor,pointDescription, sourceCell.getStringCellValue());
                        newCell.setCellValue(replacedString);
                    }else {
                        newCell.setCellValue(sourceCell.getStringCellValue().replace(standardDeviceTag, deviceTag));
                    }
                    break;
                case NUMERIC:
                    newCell.setCellValue(sourceCell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    newCell.setCellValue(sourceCell.getBooleanCellValue());
                    break;
                case FORMULA:
                    newCell.setCellFormula(sourceCell.getCellFormula());
                    break;
                case BLANK:
                    newCell.setCellType(CellType.BLANK);
                    break;
                default:
                    break;
            }
        }
    }

    public static String replaceFirstPart(String pointDescriptorRegexValue, String source, String target) {
        // Build a regex pattern using the dynamic pointDescriptorRegexValue
        String regex = "^(.*? - " + pointDescriptorRegexValue + ")";

        // Extracting the matching prefix from source
        String newPrefix = source.replaceAll(regex, "$1");

        // Replacing the first part in target string
        return target.replaceFirst(regex, newPrefix);
    }


    private String getRegexPointDescriptor(String pointDescription, String deviceTag) {
        TransformerConfiguration configuration = configurationReader.getConfiguration();
        String regex = "";
        String deviceType = DeviceTagUtility.getDeviceType(deviceTag);
        List<String> pointDescriptorsList = configuration.getDeviceKeyMapping().get(deviceType);
        if(null != pointDescriptorsList && !pointDescriptorsList.isEmpty()){
            regex = pointDescriptorsList.stream()
                    .filter(pointDescription::contains)
                    .findFirst()
                    .orElse("");
        }
        return regex;
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        return (cell != null) ? getStringCellValue(cell) : "";
    }





    public static void addHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }


    private static InputStream getReferenceFileStream() throws IOException {
        InputStream refFis = TransformerApplication.class.getClassLoader().getResourceAsStream("J270-06-Alarm-And-Parameter-list.xlsx");
        if (refFis == null) {
            throw new IOException("Reference Excel file not found in resources.");
        }
        return refFis;
    }
    private static void copyReferenceSheets(Workbook refWorkbook, Workbook newWorkbook) {
        for (Sheet refSheet : refWorkbook) {
            if (!"J270-06".equalsIgnoreCase(refSheet.getSheetName())) {
                Sheet newSheet = newWorkbook.createSheet(refSheet.getSheetName());
                copySheet(refSheet, newSheet);
            }
        }
    }

    private static void copySheet(Sheet sourceSheet, Sheet targetSheet) {
        for (Row sourceRow : sourceSheet) {
            Row targetRow = targetSheet.createRow(sourceRow.getRowNum());
            copyRow(sourceRow, targetRow);
        }
    }

    private static void copyRow(Row sourceRow, Row targetRow) {
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            if (sourceCell != null) {
                Cell targetCell = targetRow.createCell(i);
                copyCell(sourceCell, targetCell);
            }
        }
    }

    private static void copyCell(Cell sourceCell, Cell targetCell) {
        switch (sourceCell.getCellType()) {
            case STRING -> targetCell.setCellValue(sourceCell.getStringCellValue());
            case NUMERIC -> targetCell.setCellValue(sourceCell.getNumericCellValue());
            case BOOLEAN -> targetCell.setCellValue(sourceCell.getBooleanCellValue());
            case FORMULA -> targetCell.setCellFormula(sourceCell.getCellFormula());
            case BLANK -> targetCell.setBlank();
            default -> {
            }
        }
    }

    private static String getStringCellValue(Cell sourceCell) {
        String cellValue = "";
        switch (sourceCell.getCellType()) {
            case STRING -> cellValue = sourceCell.getStringCellValue();
            case NUMERIC -> cellValue = Double.toString(sourceCell.getNumericCellValue());
            case BOOLEAN -> cellValue = Boolean.toString(sourceCell.getBooleanCellValue());
            case FORMULA -> cellValue = sourceCell.getCellFormula();
            default -> {
                cellValue = "";
            }
        }
        return cellValue;
    }
}
