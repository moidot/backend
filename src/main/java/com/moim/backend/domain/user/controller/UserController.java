package com.moim.backend.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    @PostMapping("/login/kakao")
    public CustomResponseEntity<UserResponse.Login> loginByKakao(@RequestParam String authorizationCode) {
        return CustomResponseEntity.success(
                userService.loginByKakao(authorizationCode)
        );
    }

    @GetMapping("/login/naver")
    public CustomResponseEntity<UserResponse.Login> loginByNaver(@RequestParam(name = "code") String code) {
        return CustomResponseEntity.success(userService.loginByNaver(code));
    }
}
