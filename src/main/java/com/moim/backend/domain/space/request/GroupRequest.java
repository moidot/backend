package com.moim.backend.domain.space.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class GroupRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotBlank
        private String name;
        private LocalDateTime date;

        public GroupServiceRequest.Create toServiceRequest() {
            return GroupServiceRequest.Create.builder()
                    .name(name)
                    .date(date)
                    .build();
        }
    }
}
