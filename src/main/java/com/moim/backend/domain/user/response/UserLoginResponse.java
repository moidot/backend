package com.moim.backend.domain.user.response;

import com.moim.backend.domain.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {
    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;

    public static UserLoginResponse response(Users user, String token, String refreshToken) {
        return UserLoginResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }
}
