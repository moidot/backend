package com.moim.backend.domain.user.config;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.subway.response.BestPlaceInterface;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Data
@Configuration
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

    private String tokenUri;
    private String userInfoUri;
    private String grantType;
    private String clientId;
    private String redirectUri;
    private String searchPathUri;

    public MultiValueMap<String, String> getRequestParameter(String code) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", grantType);
        parameters.add("client_id", clientId);
        parameters.add("redirect_uri", redirectUri);
        parameters.add("code", code);
        return parameters;
    }

    public URI getSearchCarPathUriWithParams(BestPlaceInterface bestSubway, Participation participation) {
        return UriComponentsBuilder.fromHttpUrl(searchPathUri)
                .queryParam("origin", participation.getLongitude() + "," + participation.getLatitude())
                .queryParam("destination", bestSubway.getLongitude() + "," + bestSubway.getLatitude())
                .build()
                .toUri();
    }



}
