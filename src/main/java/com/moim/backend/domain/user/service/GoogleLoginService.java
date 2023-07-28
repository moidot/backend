package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.GoogleProperties;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.response.GoogleTokenResponse;
import com.moim.backend.domain.user.response.GoogleUserResponse;
import com.moim.backend.domain.user.response.NaverUserResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.moim.backend.global.common.Result.INVALID_ACCESS_INFO;
import static com.moim.backend.global.common.Result.NOT_AUTHENTICATE_NAVER_TOKEN_INFO;

@Service
@RequiredArgsConstructor
public class GoogleLoginService {

    private final GoogleProperties googleProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    // 유저 Entity 로 변환
    public Users toEntityUser(String code) {
        String accessToken = toRequestAccessToken(code);
        GoogleUserResponse profile = toRequestProfile(accessToken);

        return Users.builder()
                .email(profile.getEmail())
                .name(profile.getName())
                .build();
    }

    // Google AccessToken 응답
    private String toRequestAccessToken(String code) {
        // 발급받은 code -> GET 요청
        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
                googleProperties.getRequestTokenUri(),
                googleProperties.getRequestParameter(code),
                GoogleTokenResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(NOT_AUTHENTICATE_NAVER_TOKEN_INFO);
        }

        return response.getBody().getAccessToken();
    }

    // 유저 정보 응답
    private GoogleUserResponse toRequestProfile(String accessToken) {
        // accessToken 헤더 등록
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        // GET 요청으로 유저정보 응답 시도
        ResponseEntity<GoogleUserResponse> response =
                restTemplate.exchange("https://www.googleapis.com/oauth2/v1/userinfo", HttpMethod.GET, request, GoogleUserResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(INVALID_ACCESS_INFO);
        }

        return response.getBody();
    }
}
