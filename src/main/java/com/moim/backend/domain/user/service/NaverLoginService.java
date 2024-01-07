package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.NaverProperties;
import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.response.NaverTokenResponse;
import com.moim.backend.domain.user.response.NaverUserResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.moim.backend.domain.user.config.Platform.NAVER;
import static com.moim.backend.global.common.Result.NOT_AUTHENTICATE_NAVER_TOKEN_INFO;
import static com.moim.backend.global.common.Result.UNEXPECTED_EXCEPTION;
import static org.springframework.http.HttpMethod.GET;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverLoginService implements OAuth2LoginService {

    private static final String REQUEST_NAVER_USER_URL = "https://openapi.naver.com/v1/nid/me";
    private final RestTemplate restTemplate = new RestTemplate();
    private final NaverProperties naverProperties;

    @Override
    public Platform supports() {
        return NAVER;
    }

    @Override
    public Users toEntityUser(String code, Platform platform) {
        String accessToken = getNaverAccessToken(code);
        NaverUserResponse.NaverUserDetail profile = getNaverUser(accessToken);

        return Users.builder()
                .email(profile.getEmail())
                .name(profile.getName())
                .build();
    }

    @Override
    public Users toEntityUserByToken(String accessToken) {
        NaverUserResponse.NaverUserDetail profile = getNaverUser(accessToken);

        return Users.builder()
                .email(profile.getEmail())
                .name(profile.getName())
                .build();
    }

    // Naver AccessToken 응답
    private String getNaverAccessToken(String code) {
        NaverTokenResponse response = toRequestTokenNaverAccessToken(code);
        handleNaverErrorExceptions(response);
        return response.getAccessToken();
    }

    private NaverTokenResponse toRequestTokenNaverAccessToken(String code) {
        ResponseEntity<NaverTokenResponse> response =
                restTemplate.exchange(naverProperties.getRequestURL(code), GET, null, NaverTokenResponse.class);
        return response.getBody();
    }

    private static void handleNaverErrorExceptions(NaverTokenResponse response) {
        if (response.getError() != null) {
            throw new CustomException(NOT_AUTHENTICATE_NAVER_TOKEN_INFO);
        }
    }

    // 유저 정보 응답
    private NaverUserResponse.NaverUserDetail getNaverUser(String accessToken) {
        HttpEntity<?> request = createHttpEntity(accessToken);
        return toRequestNaverUser(request).getNaverUserDetail();
    }

    private NaverUserResponse toRequestNaverUser(HttpEntity<?> request) {
        ResponseEntity<NaverUserResponse> response =
                restTemplate.exchange(
                        REQUEST_NAVER_USER_URL,
                        GET,
                        request,
                        NaverUserResponse.class);

        return Optional.ofNullable(response.getBody()).orElseThrow(
                () -> new CustomException(UNEXPECTED_EXCEPTION)
        );
    }

    private static HttpEntity<?> createHttpEntity(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(headers);
    }
}
