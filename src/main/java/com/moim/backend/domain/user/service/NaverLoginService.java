package com.moim.backend.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.moim.backend.global.common.Result.INVALID_ACCESS_INFO;

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
    public String toRequestAccessToken(String code) {
        NaverTokenResponse tokenInfo =
                restTemplate.getForObject(naverProperties.getRequestURL(code), NaverTokenResponse.class);
        return "Bearer " + tokenInfo.getAccessToken();
    }

    // 유저 정보 응답
    public NaverUserResponse.NaverUserDetail toRequestProfile(String accessToken) {
        // accessToken 헤더 등록
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        // GET 요청으로 유저정보 응답 시도
        ResponseEntity<String> response =
                restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, request, String.class);

        // 응답받은 body -> Json 파싱
        try {
            NaverUserResponse naverUserResponse =
                    new ObjectMapper().readValue(response.getBody(), NaverUserResponse.class);
            return naverUserResponse.getNaverUserDetail();
        } catch (JsonProcessingException e) {
            throw new CustomException(INVALID_ACCESS_INFO);
        }

    }
}
