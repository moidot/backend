package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.response.UserLoginResponse;
import com.moim.backend.domain.user.response.UserReissueResponse;
import com.moim.backend.domain.user.service.UserService;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    // 배포 검증용 API
    @GetMapping("/success")
    public CustomResponseEntity<String> checkServerStatus() {
        return CustomResponseEntity.success("Server On!");
    }

    // 소셜 로그인 API
    @GetMapping("/signin")
    public CustomResponseEntity<UserLoginResponse> loginByOAuth(
            @RequestParam(name = "code") String code, @RequestParam Platform platform
    ) {
        return CustomResponseEntity.success(userService.loginByOAuth(code, platform));
    }

    // 엑세스 토큰 재발급
    @GetMapping("/refresh")
    public CustomResponseEntity<UserReissueResponse> refreshAccessToken(
            @RequestHeader(name = "refreshToken") String refreshToken
    ) {
        return CustomResponseEntity.success(userService.reissueAccessToken(refreshToken));
    }

    // 로그아웃 API
    @DeleteMapping("/logout")
    public CustomResponseEntity<Void> logout(
            @Login Users user,
            @RequestHeader(value = "Authorization") String atk
    ) {
        return CustomResponseEntity.success(userService.logout(user, atk));
    }
}
