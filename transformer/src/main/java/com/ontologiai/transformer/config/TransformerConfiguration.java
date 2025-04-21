package com.ontologiai.transformer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


public class TransformerConfiguration {

    private int deviceTagIndex;
    private int pointDescriptionIndex;
    private int pointDescriptionIndexInOutputFile;
    private int deviceTagIndexInOutputFile;

    @JsonProperty("deviceTagMapping")
    private Map<String, String> deviceTagMapping;

    @JsonProperty("deviceKeyMapping")
    private Map<String, List<String>> deviceKeyMapping;

    @JsonProperty("requiredHeaders")
    private List<String> requiredHeaders;

    public int getDeviceTagIndex() {
        return deviceTagIndex;
    }

    public void setDeviceTagIndex(int deviceTagIndex) {
        this.deviceTagIndex = deviceTagIndex;
    }

    public int getPointDescriptionIndex() {
        return pointDescriptionIndex;
    }

    public void setPointDescriptionIndex(int pointDescriptionIndex) {
        this.pointDescriptionIndex = pointDescriptionIndex;
    }

    public int getPointDescriptionIndexInOutputFile() {
        return pointDescriptionIndexInOutputFile;
    }

    public void setPointDescriptionIndexInOutputFile(int pointDescriptionIndexInOutputFile) {
        this.pointDescriptionIndexInOutputFile = pointDescriptionIndexInOutputFile;
    }

    public int getDeviceTagIndexInOutputFile() {
        return deviceTagIndexInOutputFile;
    }

    public void setDeviceTagIndexInOutputFile(int deviceTagIndexInOutputFile) {
        this.deviceTagIndexInOutputFile = deviceTagIndexInOutputFile;
    }

    public Map<String, String> getDeviceTagMapping() {
        return deviceTagMapping;
    }

    public void setDeviceTagMapping(Map<String, String> deviceTagMapping) {
        this.deviceTagMapping = deviceTagMapping;
    }

    public Map<String, List<String>> getDeviceKeyMapping() {
        return deviceKeyMapping;
    }

    public void setDeviceKeyMapping(Map<String, List<String>> deviceKeyMapping) {
        this.deviceKeyMapping = deviceKeyMapping;
    }

    public List<String> getRequiredHeaders() {
        return requiredHeaders;
    }

    public void setRequiredHeaders(List<String> requiredHeaders) {
        this.requiredHeaders = requiredHeaders;
    }
}
