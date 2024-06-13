package com.moim.backend.domain.space.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "tmap")
public class TmapProperties {

    private String appKey;
    private String searchPathUri;

    public Map<String, String> getRequestParameter(Double startX, Double startY, Double endX, Double endY) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("startX", String.valueOf(startX));
        parameters.put("startY", String.valueOf(startY));
        parameters.put("endX", String.valueOf(endX));
        parameters.put("endY", String.valueOf(endY));
        return parameters;
    }

}
