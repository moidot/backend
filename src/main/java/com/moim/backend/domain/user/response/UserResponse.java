package com.moim.backend.domain.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        private String token;
    }

}
