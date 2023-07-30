package com.moim.backend.domain.user.controller;

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
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public CustomResponseEntity<String> getUserNameByToken(@Login Users user) {

        return CustomResponseEntity.success(
                userService.getUserNameByToken(user)
        );
    }

    @PostMapping("/login")
    public CustomResponseEntity<UserResponse.Login> loginByEmail(@Valid @RequestBody UserRequest.Login request) {
        return CustomResponseEntity.success(
                userService.login(request)
        );
    }

    @GetMapping("/login/kakao")
    public CustomResponseEntity<UserResponse.Login> loginByKakao(@RequestParam(name = "code") String code) {
        return CustomResponseEntity.success(userService.loginByOAuth(code, KAKAO));
    }

    @GetMapping("/login/naver")
    public CustomResponseEntity<UserResponse.Login> loginByNaver(@RequestParam(name = "code") String code) {
        return CustomResponseEntity.success(userService.loginByOAuth(code, NAVER));
    }

    @GetMapping("/login/google")
    public CustomResponseEntity<UserResponse.Login> loginByGoogle(@RequestParam(name = "code") String code) {
        return CustomResponseEntity.success(userService.loginByOAuth(code, GOOGLE));
    }
}
