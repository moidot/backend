package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.domain.user.response.UserResponse;
import com.moim.backend.domain.user.service.UserService;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.moim.backend.domain.user.config.Platform.*;

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
    public CustomResponseEntity<UserResponse.Login> loginByOAuth(
            @RequestParam(name = "code") String code, @RequestParam Platform platform
    ) {
        return CustomResponseEntity.success(userService.loginByOAuth(code, platform));
    }
}
