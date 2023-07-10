package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.global.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;

    public String login(UserRequest.LoginDto request) {
        return jwtService.createToken(request.getEmail());
    }

}
