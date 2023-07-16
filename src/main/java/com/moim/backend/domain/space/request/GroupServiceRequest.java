package com.moim.backend.domain.space.request;

import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.user.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

        public Groups toGroupEntity(Users user) {
            return Groups.builder()
                    .adminId(user.getUserId())
                    .name(name)
                    .date(date)
                    .place("none")
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Participate {
        private Long groupId;
        private Double latitude;
        private Double longitude;
        private String transportation;
        private String password;

        @Builder
        private Participate(Long groupId, Double latitude, Double longitude, String transportation, String password) {
            this.groupId = groupId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.transportation = transportation;
            this.password = password;
        }

        public Participation toParticipationEntity(Groups group, Users user, String encryptedPassword) {
            return Participation.builder()
                    .group(group)
                    .userId(user.getUserId())
                    .userName(user.getName())
                    .latitude(latitude)
                    .longitude(longitude)
                    .transportation(TransportationType.valueOf(transportation))
                    .password(encryptedPassword)
                    .build();
        }
    }
}
