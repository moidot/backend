package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.NaverProperties;
import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.response.NaverTokenResponse;
import com.moim.backend.domain.user.response.NaverUserResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.util.Optional;

import static com.moim.backend.global.common.Result.*;
import static org.springframework.http.HttpMethod.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverLoginService implements OAuth2LoginService {

    private static final String REQUEST_NAVER_USER_URL = "https://openapi.naver.com/v1/nid/me";
    private final RestTemplate restTemplate = new RestTemplate();
    private final NaverProperties naverProperties;

    @Override
    public Platform supports() {
        return Platform.NAVER;
    }

    @Override
    public Users toEntityUser(String code, Platform platform) {
        String accessToken = getNaverAccessToken(code);
        NaverUserResponse.NaverUserDetail profile = toRequestProfile(accessToken);

        return Users.builder()
                .email(profile.getEmail())
                .name(profile.getName())
                .build();
    }

    // Naver AccessToken 응답
    private String getNaverAccessToken(String code) {
        try {
            NaverTokenResponse response = toRequestTokenNaverAccessToken(code);
            handleNaverErrorExceptions(response);
            return response.getAccessToken();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            handleHttpExceptions(e);
        } catch (ResourceAccessException e) {
            handleNetworkExceptions(e);
        } catch (HttpMessageNotReadableException e) {
            handleResponseParseExceptions(e);
        }
        throw new CustomException(INVALID_ACCESS_INFO);
    }

    private NaverTokenResponse toRequestTokenNaverAccessToken(String code) {
        ResponseEntity<NaverTokenResponse> response =
                restTemplate.exchange(naverProperties.getRequestURL(code), GET, null, NaverTokenResponse.class);
        return response.getBody();
    }

    // 유저 정보 응답
    private NaverUserResponse.NaverUserDetail toRequestProfile(String accessToken) {
        try {
            HttpEntity<?> request = createHttpEntity(accessToken);
            return toRequestNaverUser(request).getNaverUserDetail();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            handleHttpExceptions(e);
        } catch (ResourceAccessException e) {
            handleNetworkExceptions(e);
        } catch (HttpMessageNotReadableException e) {
            handleResponseParseExceptions(e);
        }
        throw new CustomException(INVALID_ACCESS_INFO);
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

    private static void handleNaverErrorExceptions(NaverTokenResponse response) {
        if (response.getError() != null) {
            throw new CustomException(NOT_AUTHENTICATE_NAVER_TOKEN_INFO);
        }
    }
}
