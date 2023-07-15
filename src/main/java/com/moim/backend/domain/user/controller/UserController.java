package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.user.entity.User;
import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.domain.user.response.UserResponse;
import com.moim.backend.domain.user.service.UserService;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public CustomResponseEntity<String> getUserNameByToken(@Login User user) {

        return CustomResponseEntity.success(user.getName());
    }

    @PostMapping("/login")
    public CustomResponseEntity<UserResponse.Login> loginByEmail(@RequestBody UserRequest.Login request) {
        return CustomResponseEntity.success(
                userService.login(request)
        );
    }

}
