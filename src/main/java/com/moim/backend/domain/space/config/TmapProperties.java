package com.moim.backend.domain.space.config;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Participation;
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
    private String walkSearchPathUri;

    public Map<String, String> getRequestParameter(Double startX, Double startY, Double endX, Double endY) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("startX", String.valueOf(startX));
        parameters.put("startY", String.valueOf(startY));
        parameters.put("endX", String.valueOf(endX));
        parameters.put("endY", String.valueOf(endY));
        return parameters;
    }

    public Map<String, String> getWalkRequestParameter(
            String startName, String endName, Double startX, Double startY, Double endX, Double endY
    ) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("startName", startName);
        parameters.put("endName", endName);
        parameters.put("startX", String.valueOf(startX));
        parameters.put("startY", String.valueOf(startY));
        parameters.put("endX", String.valueOf(endX));
        parameters.put("endY", String.valueOf(endY));
        return parameters;
    }

}
