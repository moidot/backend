package com.moim.backend.domain.space.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Participate {
        @NotNull
        private Long groupId;

        @NotNull
        private Double latitude;

        @NotNull
        private Double longitude;

        @NotNull
        private String transportation;

        private String password;

        public GroupServiceRequest.Participate toServiceRequest() {
            return GroupServiceRequest.Participate.builder()
                    .groupId(groupId)
                    .latitude(latitude)
                    .longitude(longitude)
                    .transportation(transportation)
                    .password(password)
                    .build();
        }
    }
}
