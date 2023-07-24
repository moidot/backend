package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.domain.user.response.KakaoTokenResponse;
import com.moim.backend.domain.user.response.KakaoUserResponse;
import com.moim.backend.domain.user.response.UserResponse;
import com.moim.backend.global.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final KakaoProperties kakaoProperties;
    private RestTemplate restTemplate = new RestTemplate();

    public String getUserNameByToken(Users user) {
        return user.getName();
    }

    public UserResponse.Login login(UserRequest.Login request) {
        Users user = saveOrUpdate(request);

        return new UserResponse.Login(jwtService.createToken(user.getEmail()));
    }

    public UserResponse.Login loginByKakao(String authorizationCode) {
        KakaoTokenResponse kakaoTokenResponse = getKakaoToken(authorizationCode);
        KakaoUserResponse kakaoUserResponse = getKakaoUserInfo(kakaoTokenResponse);
        return login(new UserRequest.Login(kakaoUserResponse));
    }

    private Users saveOrUpdate(UserRequest.Login request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .map(entity -> entity.update(request.getName()))
                .orElse(toUserEntity(request));

        return userRepository.save(user);
    }

    private Users toUserEntity(UserRequest.Login request) {
        return Users.builder()
                .email(request.getEmail())
                .name(request.getName())
                .build();
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
