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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.moim.backend.global.common.Result.UNEXPECTED_EXCEPTION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final List<OAuth2LoginService> oAuth2LoginServices;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // 소셜 로그인
    @Transactional
    public UserResponse.Login loginByOAuth(String code, Platform platform) {
        // 요청된 로그인 플랫폼 확인 후 소셜 로그인 진행
        Users userEntity = oauthLoginProcess(code, platform);

        // 현재 서비스 내 회원인지 검증 및 save
        Users user = saveOrUpdate(userEntity);

        // 서비스 JWT 토큰 발급
        String accessToken = jwtService.createToken(user.getEmail());

        return UserResponse.Login.response(user, accessToken);
    }


    // method

    private Users oauthLoginProcess(String code, Platform platform) {
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
        return userEntity;
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

}
