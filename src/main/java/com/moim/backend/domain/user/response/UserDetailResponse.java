package com.moim.backend.domain.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class UserDetailResponse {
    private Long userId;
    private String email;
    private String name;
}
