package com.ontologiai.transformer.ui;

import com.ontologiai.transformer.file.FileProcessor;
import com.ontologiai.transformer.service.SensorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

@org.springframework.stereotype.Component
public class UIUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIUtil.class);

    @Autowired
    private FileProcessor fileProcessor;

    @Autowired
    SensorService sensorService;

    private File selectedFile;

    private JLabel selectedFileLabel; // Label to display selected file

    public void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LOGGER.info("Look and feel set successfully.");
        } catch (Exception e) {
            LOGGER.error("Error setting look and feel", e);
        }
    }

    public void createAndShowGUI() {

        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Error: Running in headless mode! GUI cannot be displayed.");
            return;
        }
        JFrame frame = new JFrame("DVL TO AVL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        // Create Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add "Home" Tab
        tabbedPane.addTab("Home", createHomePanel(frame));

        // Add "Settings" Tab
        tabbedPane.addTab("Settings", createSettingsPanel(frame));

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private JPanel createHomePanel(JFrame frame) {
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = createLabel("Company Name (TODO)", Font.BOLD, 20);
        JButton uploadButton = createButton("DVL To AVL");

        uploadButton.addActionListener(e -> fileProcessor.handleFileUpload(frame));

        homePanel.add(headerLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        homePanel.add(uploadButton);

        return homePanel;
    }

    private JPanel createSettingsPanel(JFrame frame) {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel settingsLabel = createLabel("Upload Standard APL CSV File", Font.BOLD, 18);

        JButton uploadCSVButton = createButton("Choose Standard APL  CSV File");
        JButton processCSVButton = createButton("Process Standard APL CSV");

        selectedFileLabel = createLabel("No file selected", Font.ITALIC, 14);

        uploadCSVButton.addActionListener(e -> selectCSVFile(frame));
        processCSVButton.addActionListener(e -> processCSVFile());

        settingsPanel.add(settingsLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(uploadCSVButton);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        settingsPanel.add(selectedFileLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(processCSVButton);

        return settingsPanel;
    }

    private void processCSVFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(null, "Please select a CSV file first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LOGGER.info("Processing CSV File: {}", selectedFile.getAbsolutePath());


        // Process the file using FileProcessor
        try {
            sensorService.saveSensorsFromExcel(selectedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JOptionPane.showMessageDialog(null, "CSV Processing Completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void selectCSVFile(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a CSV File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            selectedFileLabel.setText("Selected: " + selectedFile.getName());
            LOGGER.info("CSV File Selected: {}", selectedFile.getAbsolutePath());
        }
    }

    private JLabel createLabel(String text, int style, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(10, 15, 10, 15)
        ));
        return button;
    }
}
