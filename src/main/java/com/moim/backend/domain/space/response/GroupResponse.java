package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import lombok.*;

import java.time.format.DateTimeFormatter;

public class GroupResponse {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private Long groupId;
        private Long adminId;
        private String name;
        private String date;
        private String fixedPlace;

        @Builder
        private Create(Long groupId, Long adminId, String name, String date, String fixedPlace) {
            this.groupId = groupId;
            this.adminId = adminId;
            this.name = name;
            this.date = date;
            this.fixedPlace = fixedPlace;
        }

        public static GroupResponse.Create response(Groups group) {
            String date;
            if (group.getDate() != null) date = group.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            else date = "none";
            return Create.builder()
                    .groupId(group.getGroupId())
                    .adminId(group.getAdminId())
                    .name(group.getName())
                    .date(date)
                    .fixedPlace(group.getPlace())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Participate {
        private Long participationId;
        private Long groupId;
        private Long userId;
        private String userName;
        private String locationName;
        private Double latitude;
        private Double longitude;
        private String transportation;

        @Builder
        private Participate(Long participationId, Long groupId, Long userId, String userName, String locationName, Double latitude, Double longitude, String transportation) {
            this.participationId = participationId;
            this.groupId = groupId;
            this.userId = userId;
            this.userName = userName;
            this.locationName = locationName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.transportation = transportation;
        }

        public static GroupResponse.Participate response(Participation participation) {
            return Participate.builder()
                    .participationId(participation.getParticipationId())
                    .groupId(participation.getGroup().getGroupId())
                    .userId(participation.getUserId())
                    .userName(participation.getUserName())
                    .locationName(participation.getLocationName())
                    .latitude(participation.getLatitude())
                    .longitude(participation.getLongitude())
                    .transportation(participation.getTransportation().name())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class ParticipateUpdate {
        private String locationName;
        private String transportation;

        public static GroupResponse.ParticipateUpdate response(Participation participation) {
            return ParticipateUpdate.builder()
                    .locationName(participation.getLocationName())
                    .transportation(participation.getTransportation().name())
                    .build();
        }
    }
}
