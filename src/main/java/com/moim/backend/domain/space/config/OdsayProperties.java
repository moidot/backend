package com.moim.backend.domain.space.config;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.subway.response.BestPlaceInterface;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@Data
@Configuration
@ConfigurationProperties(prefix = "odsay")
public class OdsayProperties {

    private String apiKey;
    private String searchPathUri;
    private String graphicDataUri;

    public URI getSearchPathUriWithParams(double startX, double startY, double destinationX, double destinationY) {
        return UriComponentsBuilder.fromHttpUrl(searchPathUri)
                .queryParam("apiKey", apiKey)
                .queryParam("SX", startX)
                .queryParam("SY", startY)
                .queryParam("EX", destinationX)
                .queryParam("EY", destinationY)
                .build()
                .toUri();
    }

    public URI getSearchPathUriWithParams(BestPlaceInterface bestSubway, Participation participation) {
        return UriComponentsBuilder.fromHttpUrl(searchPathUri)
                .queryParam("apiKey", apiKey)
                .queryParam("SX", participation.getLongitude())
                .queryParam("SY", participation.getLatitude())
                .queryParam("EX", bestSubway.getLongitude())
                .queryParam("EY", bestSubway.getLatitude())
                .build()
                .toUri();
    }

    public URI getGraphicDataUriWIthParams(String mapObj) {
        return UriComponentsBuilder.fromHttpUrl(graphicDataUri)
                .queryParam("apiKey", apiKey)
                .queryParam("mapObject", mapObj)
                .build()
                .toUri();
    }

}
