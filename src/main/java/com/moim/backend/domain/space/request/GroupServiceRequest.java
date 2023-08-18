package com.moim.backend.domain.space.request;

import com.moim.backend.domain.space.entity.TransportationType;
import lombok.*;

import java.time.LocalDate;

public class GroupServiceRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String name;
        private LocalDate date;

        @Builder
        private Create(String name, LocalDate date) {
            this.name = name;
            this.date = date;
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @Builder
    public static class Participate {
        private Long groupId;
        private String userName;
        private String locationName;
        private Double latitude;
        private Double longitude;
        private TransportationType transportationType;
        private String password;
    }


    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @Builder
    public static class ParticipateUpdate {
        private Long participateId;
        private String userName;
        private String locationName;
        private Double latitude;
        private Double longitude;
        private TransportationType transportationType;
    }
}
