package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.domain.user.service.UserService;
import com.moim.backend.global.common.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public CustomResponseEntity<String> loginByEmail(@RequestBody UserRequest.Login request) {
        return CustomResponseEntity.success(
                userService.login(request)
        );
    }

    @PostMapping("/login/security")
    public CustomResponseEntity<String> loginByToken() {
        return CustomResponseEntity.success();
    }

}
