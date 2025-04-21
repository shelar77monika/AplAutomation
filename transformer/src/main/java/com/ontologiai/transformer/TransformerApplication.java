package com.ontologiai.transformer;

import com.ontologiai.transformer.file.FileUtil;
import com.ontologiai.transformer.ui.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

@SpringBootApplication
public class TransformerApplication {

	private static final Logger logger = LoggerFactory.getLogger(TransformerApplication.class);
	public static final String UPLOAD_DIR = "uploads/";

	@Autowired
	FileUtil fileUtil;

	@Autowired
	UIUtil uIUtil;

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(TransformerApplication.class, args);
	}

	@Bean
	public CommandLineRunner createGUI() {

		return args -> SwingUtilities.invokeLater(() -> {
			logger.info("Application starting...");
			fileUtil.ensureDirectoryExists(UPLOAD_DIR);
			uIUtil.setLookAndFeel();
			uIUtil.createAndShowGUI();
			logger.info("Application started successfully.");
			System.out.println("Application is started");
		});
	}

}
