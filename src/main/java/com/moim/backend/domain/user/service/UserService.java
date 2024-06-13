package com.moim.backend.domain.user.service;

import com.moim.backend.domain.bookmark.repository.BookmarkRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.domain.user.response.UserLoginResponse;
import com.moim.backend.domain.user.response.UserReissueResponse;
import com.moim.backend.global.auth.jwt.JwtService;
import com.moim.backend.global.common.RedisService;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.moim.backend.global.common.Result.FAIL_SOCIAL_LOGIN;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final List<OAuth2LoginService> oAuth2LoginServices;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ParticipationRepository participationRepository;
    private final JwtService jwtService;
    private final RedisService redisService;

    // 소셜 로그인 API
    @Transactional
    public UserLoginResponse loginByOAuth(String code, Platform platform) {
        // 요청된 로그인 플랫폼 확인 후 소셜 로그인 진행
        Users userEntity = oauthLoginProcess(code, platform);

        // 현재 서비스 내 회원인지 검증 및 save
        Users user = saveOrUpdate(userEntity);

        // 서비스 JWT 토큰 발급
        String accessToken = jwtService.createAccessToken(user.getEmail());
        String refreshToken = jwtService.createRefreshToken(user.getEmail());

        return UserLoginResponse.response(user, accessToken, refreshToken);
    }

    private Users oauthLoginProcess(String code, Platform platform) {
        return getOptionalSocialUserEntity(code, platform)
                .orElseThrow(() -> new CustomException(FAIL_SOCIAL_LOGIN));
    }

    private Optional<Users> getOptionalSocialUserEntity(String code, Platform platform) {
        for (OAuth2LoginService oAuth2LoginService : oAuth2LoginServices) {
            if (oAuth2LoginService.supports().equals(platform)) {
                return Optional.of(oAuth2LoginService.toEntityUser(code, platform));
            }
        }
        return Optional.empty();
    }

    // 소셜 로그인 (accessToken) API
    @Transactional
    public UserLoginResponse loginByAccessToken(String token, Platform platform) {
        // 요청된 로그인 플랫폼 확인 후 소셜 로그인 진행
        Users userEntity = oauthLoginProcessByToken(token, platform);

        // 현재 서비스 내 회원인지 검증 및 save
        Users user = saveOrUpdate(userEntity);

        // 서비스 JWT 토큰 발급
        String accessToken = jwtService.createAccessToken(user.getEmail());
        String refreshToken = jwtService.createRefreshToken(user.getEmail());

        return UserLoginResponse.response(user, accessToken, refreshToken);
    }

    private Users oauthLoginProcessByToken(String token, Platform platform) {
        return getOptionalSocialUserEntityByToken(token, platform)
                .orElseThrow(() -> new CustomException(FAIL_SOCIAL_LOGIN));
    }

    private Optional<Users> getOptionalSocialUserEntityByToken(String accessToken, Platform platform) {
        for (OAuth2LoginService oAuth2LoginService : oAuth2LoginServices) {
            if (oAuth2LoginService.supports().equals(platform)) {
                return Optional.of(oAuth2LoginService.toEntityUserByToken(accessToken));
            }
        }
        return Optional.empty();
    }

    // 액세스 토큰 재발급 API
    public UserReissueResponse reissueAccessToken(String refreshToken) {
        return UserReissueResponse.toResponse(jwtService.reissueAccessToken(refreshToken));
    }

    // 로그아웃 API
    public Void logout(Users user, String authorization) {
        String token = jwtService.getToken(Optional.of(authorization));
        Long expiration = jwtService.getExpiration(token);

        return redisService.logoutFromRedis(user.getEmail(), token, expiration);
    }

    // 회원탈퇴 API
    @Transactional
    public Void deleteAccount(Users user) {
        Long userId = user.getUserId();
        bookmarkRepository.deleteByUserId(userId);
        participationRepository.deleteByUserId(userId);
        userRepository.delete(user);
        return null;
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
