package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.domain.user.response.UserResponse;
import com.moim.backend.global.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    public String getUserNameByToken(Users user) {
        return user.getName();
    }

    public UserResponse.Login login(UserRequest.Login request) {
        Users user = saveOrUpdate(request);

        return new UserResponse.Login(jwtService.createToken(user.getEmail()));
    }

    private Users saveOrUpdate(UserRequest.Login request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .map(entity -> entity.update(request.getName()))
                .orElse(toUserEntity(request));

        return userRepository.save(user);
    }

    private Users toUserEntity(UserRequest.Login request) {
        return Users.builder()
                .email(request.getEmail())
                .name(request.getName())
                .build();
    }

}
