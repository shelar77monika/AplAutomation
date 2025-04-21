package com.ontologiai.transformer.transformation;

import com.ontologiai.transformer.config.ConfigurationReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DeviceTagMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceTagMapper.class.getName());


    private ConfigurationReader configurationReader;

    public DeviceTagMapper(@Autowired ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }


    public String getStandardDeviceTag(String deviceTag, String pointDescription, String deviceType) {
        if ("J460-02-2TT-717".equalsIgnoreCase(deviceTag)) {
            LOGGER.info("Here we need to debug...");
        }

        String keyPrefix = DeviceTagUtility.getDeviceType(deviceTag);

        String deviceKeyPostfix = getDeviceKey(keyPrefix, pointDescription);
        String deviceKey = keyPrefix + "-" + deviceType + "-"+ deviceKeyPostfix;

        return configurationReader.getConfiguration().getDeviceTagMapping().getOrDefault(deviceKey, "");
    }

    private String getDeviceKey(String keyPrefix, String pointDescriptor) {
        if (pointDescriptor == null) return "";

        return Optional.ofNullable(configurationReader.getConfiguration().getDeviceKeyMapping().get(keyPrefix))
                .flatMap(keys -> findMatchingKey(keys, pointDescriptor))
                .orElse("");
    }

    private static Optional<String> findMatchingKey(List<String> keys, String descriptor) {
        for (String key : keys) {
            if (descriptor.contains(key)) {
                return Optional.of(key);
            }
        }
        return Optional.empty();
    }

}
