package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.service.UserService;
import com.moim.backend.global.auth.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    // 유저 상세 조회 API
    @GetMapping("")
    public void getUser(
            @Login Users user
    ) {

    }
}
