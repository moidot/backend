package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.domain.user.response.UserResponse;
import com.moim.backend.global.auth.jwt.JwtService;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.moim.backend.global.common.Result.UNEXPECTED_EXCEPTION;

@Service
@RequiredArgsConstructor
public class UserService {

    private final List<OAuth2LoginService> oAuth2LoginServices;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public String getUserNameByToken(Users user) {
        return user.getName();
    }

    // 테스트용 이메일 로그인
    public UserResponse.Login login(UserRequest.Login request) {
        Users user = saveOrUpdate(request);

        return UserResponse.Login.builder()
                .email(request.getEmail())
                .name(request.getName())
                .token(jwtService.createToken(user.getEmail()))
                .build();
    }

    // 소셜 로그인
    public UserResponse.Login loginByOAuth(String code, Platform platform) {
        // 요청된 로그인 플랫폼 확인 후 소셜 로그인 진행
        Users userEntity = null;

        for (OAuth2LoginService oAuth2LoginService : oAuth2LoginServices) {
            if (oAuth2LoginService.supports().equals(platform)) {
                userEntity = oAuth2LoginService.toEntityUser(code, platform);
                break;
            }
        }

        if (userEntity == null) {
            throw new CustomException(UNEXPECTED_EXCEPTION);
        }

        // 현재 서비스 내 회원인지 검증 및 save
        Users user = saveOrUpdate(userEntity);

        // 서비스 JWT 토큰 발급
        String accessToken = jwtService.createToken(user.getEmail());

        return UserResponse.Login.builder()
                .email(user.getEmail())
                .name(user.getName())
                .token(accessToken)
                .build();
    }

    // method

    private Users saveOrUpdate(UserRequest.Login request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .map(entity -> entity.update(request.getName()))
                .orElse(toUserEntity(request));

        return userRepository.save(user);
    }

    private Users saveOrUpdate(Users userEntity) {
        Users user = userRepository.findByEmail(userEntity.getEmail())
                .map(entity -> entity.update(userEntity.getName()))
                .orElse(toUserEntity(userEntity));

        return userRepository.save(user);
    }

    private Users toUserEntity(Users request) {
        return Users.builder()
                .email(request.getEmail())
                .name(request.getName())
                .build();
    }

    private Users toUserEntity(UserRequest.Login request) {
        return Users.builder()
                .email(request.getEmail())
                .name(request.getName())
                .build();
    }
}
