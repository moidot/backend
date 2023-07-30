package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.NaverProperties;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.response.NaverTokenResponse;
import com.moim.backend.domain.user.response.NaverUserResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.moim.backend.global.common.Result.*;

@Service
@RequiredArgsConstructor
public class NaverLoginService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final NaverProperties naverProperties;

    // 유저 Entity 로 변환
    public Users toEntityUser(String code) {
        String accessToken = toRequestAccessToken(code);
        NaverUserResponse.NaverUserDetail profile = toRequestProfile(accessToken);

        return Users.builder()
                .email(profile.getEmail())
                .name(profile.getName())
                .build();
    }

    // Naver AccessToken 응답
    private String toRequestAccessToken(String code) {
        // 발급받은 code -> GET 요청
        ResponseEntity<NaverTokenResponse> response =
                restTemplate.exchange(naverProperties.getRequestURL(code), HttpMethod.GET, null, NaverTokenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(NOT_FOUND_NAVER_LOGIN);
        }
        if (response.getBody().getError() != null) {
            throw new CustomException(NOT_AUTHENTICATE_NAVER_TOKEN_INFO);
        }

        return response.getBody().getAccessToken();
    }

    // 유저 정보 응답
    private NaverUserResponse.NaverUserDetail toRequestProfile(String accessToken) {
        // accessToken 헤더 등록
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        // GET 요청으로 유저정보 응답 시도
        ResponseEntity<NaverUserResponse> response =
                restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, request, NaverUserResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(INVALID_ACCESS_INFO);
        }

        return response.getBody().getNaverUserDetail();
    }
}
