package com.moim.backend.domain.space.request;

import com.moim.backend.domain.space.entity.TransportationType;
import lombok.*;

import java.time.LocalDate;

public class GroupServiceRequest {

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
