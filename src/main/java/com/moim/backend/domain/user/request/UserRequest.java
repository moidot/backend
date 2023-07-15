package com.moim.backend.domain.user.request;

import com.moim.backend.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        @NotNull(message = "email은 null이 될 수 없습니다.")
        private String email;
        @NotNull(message = "name은 null이 될 수 없습니다.")
        private String name;

        public User toUserEntity() {
            return User.builder()
                    .email(email)
                    .name(name)
                    .build();
        }
    }

}
