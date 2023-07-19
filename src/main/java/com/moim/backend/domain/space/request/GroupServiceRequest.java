package com.moim.backend.domain.space.request;

import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.user.entity.Users;
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
        private String transportation;
        private String password;
    }
}
