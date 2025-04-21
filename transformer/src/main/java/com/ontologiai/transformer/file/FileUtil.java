package com.ontologiai.transformer.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public void ensureDirectoryExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() && dir.mkdirs()) {
            LOGGER.info("Directory created: {}", dirPath);
        } else if (!dir.exists()) {
            LOGGER.error("Failed to create directory: {}", dirPath);
        }
    }
}
