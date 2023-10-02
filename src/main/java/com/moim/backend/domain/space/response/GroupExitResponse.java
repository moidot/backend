package com.moim.backend.domain.space.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@Builder
public class GroupExitResponse {
    private Boolean isDeletedSpace;
    private String message;

    public static GroupExitResponse response(Boolean isDeletedSpace, String message) {
        return GroupExitResponse.builder()
                .isDeletedSpace(isDeletedSpace)
                .message(message)
                .build();
    }
}
