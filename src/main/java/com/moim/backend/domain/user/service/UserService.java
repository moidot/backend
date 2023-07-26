package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.domain.user.config.NaverProperties;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.domain.user.response.*;
import com.moim.backend.global.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final NaverLoginService naverLoginService;
    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public String getUserNameByToken(Users user) {
        return user.getName();
    }

    public UserResponse.Login login(UserRequest.Login request) {
        Users user = saveOrUpdate(request);

        return UserResponse.Login.builder()
                .email(request.getEmail())
                .name(request.getName())
                .token(jwtService.createToken(user.getEmail()))
                .build();
    }

    public UserResponse.Login loginByKakao(String authorizationCode) {
        KakaoTokenResponse kakaoTokenResponse = getKakaoToken(authorizationCode);
        KakaoUserResponse kakaoUserResponse = getKakaoUserInfo(kakaoTokenResponse);
        return login(new UserRequest.Login(kakaoUserResponse));
    }

    public UserResponse.Login loginByNaver(String code) {
        // 네이버 로그인 진행
        Users user = naverLoginService.toEntityUser(code);

        // 현재 서비스 내 회원인지 검증
        Boolean isUser = userRepository.existsByEmail(user.getEmail());
        if (!isUser) {
            userRepository.save(user);
        }

        // 서비스 JWT 토큰 발급
        String accessToken = jwtService.createToken(user.getEmail());

        return UserResponse.Login.builder()
                .email(user.getEmail())
                .name(user.getName())
                .token(accessToken)
                .build();
    }


    // method
    private Users toUserEntity(UserRequest.Login request) {
        return Users.builder()
                .email(request.getEmail())
                .name(request.getName())
                .build();
    }

    private Users saveOrUpdate(UserRequest.Login request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .map(entity -> entity.update(request.getName()))
                .orElse(toUserEntity(request));

        return userRepository.save(user);
    }

    private KakaoTokenResponse getKakaoToken(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", kakaoProperties.getGrantType());
        params.add("client_id", kakaoProperties.getClientId());
        params.add("redirect_uri", kakaoProperties.getRedirectUri());
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.postForObject(
                kakaoProperties.getTokenUri(), request, KakaoTokenResponse.class
        );
    }

    private KakaoUserResponse getKakaoUserInfo(KakaoTokenResponse kakaoTokenResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(kakaoTokenResponse.getAccessToken());

        return restTemplate.postForObject(
                kakaoProperties.getUserInfoUri(), new HttpEntity<>(headers), KakaoUserResponse.class
        );
    }
}
