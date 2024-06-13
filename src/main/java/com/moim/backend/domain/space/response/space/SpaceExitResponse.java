package com.moim.backend.domain.space.response.space;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@Builder
public class SpaceExitResponse {
    private Boolean isDeletedSpace;
    private String message;

    public static SpaceExitResponse response(Boolean isDeletedSpace, String message) {
        return SpaceExitResponse.builder()
                .isDeletedSpace(isDeletedSpace)
                .message(message)
                .build();
    }
}
