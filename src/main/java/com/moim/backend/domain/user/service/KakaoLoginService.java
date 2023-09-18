package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.response.KakaoTokenResponse;
import com.moim.backend.domain.user.response.KakaoUserResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.util.Optional;

import static com.moim.backend.global.common.Result.*;
import static org.springframework.http.MediaType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginService implements OAuth2LoginService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final KakaoProperties kakaoProperties;

    @Override
    public Platform supports() {
        return Platform.KAKAO;
    }

    @Override
    public Users toEntityUser(String code, Platform platform) {
        String accessToken = getKakaoAccessToken(code);
        KakaoUserResponse profile = getKakaoUser(accessToken);

        return Users.builder()
                .email(profile.getKakaoAccount().getEmail())
                .name(profile.getProperties().getNickname())
                .build();
    }

    // Kakao AccessToken 응답
    private String getKakaoAccessToken(String code) {
        try {
            HttpHeaders headers = createHttpEntity();
            return toRequestKakaoAccessToken(code, headers).getAccessToken();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            handleHttpExceptions(e);
        } catch (ResourceAccessException e) {
            handleNetworkExceptions(e);
        } catch (HttpMessageNotReadableException e) {
            handleResponseParseExceptions(e);
        }
        throw new CustomException(INVALID_ACCESS_INFO);
    }

    private KakaoTokenResponse toRequestKakaoAccessToken(String code, HttpHeaders headers) {
        ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                kakaoProperties.getTokenUri(),
                new HttpEntity<>(kakaoProperties.getRequestParameter(code), headers),
                KakaoTokenResponse.class
        );
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new CustomException(UNEXPECTED_EXCEPTION));

    }

    private static HttpHeaders createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        return headers;
    }

    // 유저 정보 응답
    private KakaoUserResponse getKakaoUser(String accessToken) {
        try {
            HttpEntity<?> httpEntity = createHttpEntity(accessToken);
            return toRequestKakaoUser(httpEntity);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            handleHttpExceptions(e);
        } catch (ResourceAccessException e) {
            handleNetworkExceptions(e);
        } catch (HttpMessageNotReadableException e) {
            handleResponseParseExceptions(e);
        }
        throw new CustomException(INVALID_ACCESS_INFO);
    }

    private KakaoUserResponse toRequestKakaoUser(HttpEntity<?> httpEntity) {
        ResponseEntity<KakaoUserResponse> response = restTemplate.postForEntity(
                kakaoProperties.getUserInfoUri(),
                httpEntity,
                KakaoUserResponse.class
        );
        return response.getBody();
    }

    private static HttpEntity<?> createHttpEntity(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
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
}
