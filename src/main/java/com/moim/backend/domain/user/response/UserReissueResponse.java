package com.moim.backend.domain.user.response;

import lombok.*;

import static lombok.AccessLevel.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class UserReissueResponse {
    private String accessToken;

    public static UserReissueResponse toResponse(String accessToken) {
        return new UserReissueResponse(accessToken);
    }
}
