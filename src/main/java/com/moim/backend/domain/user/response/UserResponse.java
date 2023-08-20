package com.moim.backend.domain.user.response;

import com.moim.backend.domain.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        private String email;
        private String name;
        private String token;

        public static Login response(Users user, String token) {
            return Login.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .token(token)
                    .build();
        }
    }

}
