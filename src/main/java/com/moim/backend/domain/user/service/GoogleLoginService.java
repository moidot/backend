package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.GoogleProperties;
import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.response.GoogleTokenResponse;
import com.moim.backend.domain.user.response.GoogleUserResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.moim.backend.domain.user.config.Platform.GOOGLE;
import static com.moim.backend.global.common.Result.UNEXPECTED_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleLoginService implements OAuth2LoginService {

    public static final String GOOGLE_REQUEST_USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private final GoogleProperties googleProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Platform supports() {
        return GOOGLE;
    }

    @Override
    public Users toEntityUser(String code, Platform platform) {
        String accessToken = getGoogleAccessToken(URLDecoder.decode(code, StandardCharsets.UTF_8));
        GoogleUserResponse profile = getGoogleUser(accessToken);

        return Users.builder()
                .email(profile.getEmail())
                .name(profile.getName())
                .build();
    }

    // Google AccessToken 반환
    private String getGoogleAccessToken(String decodedCode) {
        return toRequestGoogleToken(decodedCode).getAccessToken();
    }

    private GoogleTokenResponse toRequestGoogleToken(String decode) {
        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
                googleProperties.getRequestTokenUri(),
                googleProperties.getRequestParameter(decode),
                GoogleTokenResponse.class);

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new CustomException(UNEXPECTED_EXCEPTION));
    }

    // 유저 정보 반환
    private GoogleUserResponse getGoogleUser(String accessToken) {
        HttpEntity<?> httpEntity = createHttpEntity(accessToken);
        return toRequestGoogleUser(httpEntity).getBody();
    }

    private ResponseEntity<GoogleUserResponse> toRequestGoogleUser(HttpEntity<?> request) {
        return restTemplate.exchange(
                GOOGLE_REQUEST_USER_INFO_URL,
                HttpMethod.GET,
                request,
                GoogleUserResponse.class
        );
    }

    private static HttpEntity<?> createHttpEntity(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(headers);
    }

}
