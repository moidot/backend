package com.moim.backend.domain.space.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class GroupServiceRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String name;
        private LocalDateTime date;

        @Builder
        private Create(String name, LocalDateTime date) {
            this.name = name;
            this.date = date;
        }
    }
}
