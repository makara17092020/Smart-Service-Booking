package com.mekong.smart_service_booking.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableConfigurationProperties(CloudinaryProperties.class)
public class CloudinaryConfig {

    private final CloudinaryProperties props;

    public CloudinaryConfig(CloudinaryProperties props) {
        this.props = props;
    }

    @Bean
    public Cloudinary cloudinary() {
        String cloudName = props.getCloudName();
        String apiKey = props.getApiKey();
        String apiSecret = props.getApiSecret();

    // ObjectUtils.asMap returns a raw Map; copy into a typed Map<String,Object>
    @SuppressWarnings("rawtypes")
    Map raw = ObjectUtils.asMap(
        "cloud_name", cloudName,
        "api_key", apiKey,
        "api_secret", apiSecret,
        "secure", true
    );
        Map<String, Object> config = new HashMap<>();
        for (Object k : raw.keySet()) {
            Object v = raw.get(k);
            config.put(k == null ? "null" : String.valueOf(k), v);
        }
        Logger log = LoggerFactory.getLogger(CloudinaryConfig.class);
        if (cloudName == null || cloudName.isBlank() || apiKey == null || apiKey.isBlank() || apiSecret == null || apiSecret.isBlank()) {
            log.warn("Cloudinary credentials are not set. Cloudinary uploads will fail until credentials are provided. Set cloudinary.cloud-name, cloudinary.api-key and cloudinary.api-secret in environment or application properties.");
        }

        return new Cloudinary(config);
    }
}