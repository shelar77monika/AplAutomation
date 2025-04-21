package com.ontologiai.transformer.file;

import com.ontologiai.transformer.TransformerApplication;
import com.ontologiai.transformer.transformation.ExcelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

@Component
public class FileProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessor.class);

    @Autowired
    private ExcelProcessor excelProcessor;

    public void handleFileUpload(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            processFile(fileChooser.getSelectedFile());
        }
    }

    public void processFile(File file) {
        if (!(file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx"))) {
            LOGGER.warn("Invalid file format: {}", file.getName());
            return;
        }

        String newFileName = "Processed_APL_For_" + file.getName();
        File outputFile = new File(TransformerApplication.UPLOAD_DIR + newFileName);

        try {
            LOGGER.info("Processing file: {}", file.getName());
            excelProcessor.readAndWriteExcelFile(file, outputFile);
            LOGGER.info("File processed successfully: {} at location {}", newFileName, outputFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Failed to process file: {}", file.getName(), e);
        }
    }
}
