package com.ontologiai.transformer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class ConfigurationReader {

    private static final String CONFIG_FILE = "config.json"; // Change as per your file location

    @Bean
    public TransformerConfiguration getConfiguration() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = new ClassPathResource(CONFIG_FILE).getInputStream()) {
            return objectMapper.readValue(inputStream, TransformerConfiguration.class);
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }
}
