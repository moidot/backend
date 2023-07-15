package com.moim.backend.domain.user.request;

import com.moim.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        private String email;
        private String name;

        public User toUserEntity() {
            return User.builder()
                    .email(email)
                    .name(name)
                    .build();
        }
    }

}
