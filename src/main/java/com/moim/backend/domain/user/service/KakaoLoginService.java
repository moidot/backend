package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.response.KakaoTokenResponse;
import com.moim.backend.domain.user.response.KakaoUserResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.moim.backend.global.common.Result.*;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final KakaoProperties kakaoProperties;

    // 유저 Entity 로 변환
    public Users toEntityUser(String code) {
        String accessToken = toRequestAccessToken(code);
        KakaoUserResponse profile = toRequestProfile(accessToken);

        return Users.builder()
                .email(profile.getKakaoAccount().getEmail())
                .name(profile.getProperties().getNickname())
                .build();
    }

    // Kakao AccessToken 응답
    private String toRequestAccessToken(String code) {
        // 발급받은 code -> POST 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                kakaoProperties.getTokenUri(),
                new HttpEntity<>(kakaoProperties.getRequestParameter(code), headers),
                KakaoTokenResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(NOT_AUTHENTICATE_NAVER_TOKEN_INFO);
        }

        return response.getBody().getAccessToken();
    }

    // 유저 정보 응답
    private KakaoUserResponse toRequestProfile(String accessToken) {
        // accessToken 헤더 등록
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        // GET 요청으로 유저정보 응답 시도
        ResponseEntity<KakaoUserResponse> response = restTemplate.postForEntity(
                kakaoProperties.getUserInfoUri(), new HttpEntity<>(headers), KakaoUserResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(INVALID_ACCESS_INFO);
        }

        return response.getBody();
    }

}
