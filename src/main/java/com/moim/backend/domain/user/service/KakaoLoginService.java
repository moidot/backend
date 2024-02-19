package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.response.kakao.KakaoTokenResponse;
import com.moim.backend.domain.user.response.kakao.KakaoUserResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.moim.backend.global.common.Result.UNEXPECTED_EXCEPTION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

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

    @Override
    public Users toEntityUserByToken(String accessToken) {
        KakaoUserResponse profile = getKakaoUser(accessToken);

        return Users.builder()
                .email(profile.getKakaoAccount().getEmail())
                .name(profile.getProperties().getNickname())
                .build();
    }

    // Kakao AccessToken 응답
    private String getKakaoAccessToken(String code) {
        HttpEntity<?> httpEntity = createHttpEntityWithCode(code);
        return toRequestKakaoAccessToken(httpEntity).getAccessToken();
    }

    private KakaoTokenResponse toRequestKakaoAccessToken(HttpEntity<?> httpEntity) {
        ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                kakaoProperties.getTokenUri(),
                httpEntity,
                KakaoTokenResponse.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new CustomException(UNEXPECTED_EXCEPTION));

    }

    private HttpEntity<?> createHttpEntityWithCode(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(kakaoProperties.getRequestParameter(code), headers);
    }

    // 유저 정보 응답
    private KakaoUserResponse getKakaoUser(String accessToken) {
        HttpEntity<?> httpEntity = createHttpEntityWithToken(accessToken);
        return toRequestKakaoUser(httpEntity);
    }

    private KakaoUserResponse toRequestKakaoUser(HttpEntity<?> httpEntity) {
        ResponseEntity<KakaoUserResponse> response = restTemplate.postForEntity(
                kakaoProperties.getUserInfoUri(),
                httpEntity,
                KakaoUserResponse.class
        );
        return response.getBody();
    }

    private static HttpEntity<?> createHttpEntityWithToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(headers);
    }
}
