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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.moim.backend.domain.user.config.Platform.GOOGLE;
import static com.moim.backend.global.common.Result.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleLoginService implements OAuth2LoginService {

    private final GoogleProperties googleProperties;
    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public Platform supports() {
        return GOOGLE;
    }

    @Override
    public Users toEntityUser(String code, Platform platform) {
        String accessToken = getGoogleAccessToken(URLDecoder.decode(code, StandardCharsets.UTF_8));
        GoogleUserResponse profile = toRequestProfile(accessToken);

        return Users.builder()
                .email(profile.getEmail())
                .name(profile.getName())
                .build();
    }

    // Google AccessToken 반환
    private String getGoogleAccessToken(String decodedCode) {
        try {
            return toRequestGoogleServer(decodedCode).getAccessToken();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            handleHttpExceptions(e);
        } catch (ResourceAccessException e) {
            handleNetworkExceptions(e);
        } catch (HttpMessageNotReadableException e) {
            handleResponseParseExceptions(e);
        }
        throw new CustomException(UNEXPECTED_EXCEPTION);
    }

    // Google 서버에 Token 응답 요청
    private GoogleTokenResponse toRequestGoogleServer(String decode) {
        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
                googleProperties.getRequestTokenUri(),
                googleProperties.getRequestParameter(decode),
                GoogleTokenResponse.class);

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new CustomException(UNEXPECTED_EXCEPTION));
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

    private void handleHttpExceptions(HttpStatusCodeException e) {
        log.error("HTTP error occurred: {}", e.getStatusCode(), e);
        throw new CustomException(FAIL_REQUEST_ACCESS_TOKEN);
    }

    private void handleNetworkExceptions(ResourceAccessException e) {
        log.error("Network issue: {}", e.getMessage(), e);
        throw new CustomException(FAIL_REQUEST_TIME_OUT);
    }

    private void handleResponseParseExceptions(HttpMessageNotReadableException e) {
        log.error("Unparseable response body: {}", e.getMessage(), e);
        throw new CustomException(NOT_MATCH_RESPONSE);
    }
}
