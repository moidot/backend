package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Detail {
        private Long groupId;
        private Long adminId;
        private String name;
        private String date;
        private List<Region> participantsByRegion;

        public static Detail response(Groups group, List<Region> participantsByRegion) {
            String date = Optional.ofNullable(group.getDate())
                    .map(d -> d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .orElse(null);

            return Detail.builder()
                    .groupId(group.getGroupId())
                    .name(group.getName())
                    .adminId(group.getAdminId())
                    .date(date)
                    .participantsByRegion(participantsByRegion)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Region {
        private String regionName;

        @Setter
        private List<Participations> participations;

        public static Region toLocalEntity(String region, GroupResponse.Participations participation) {
            return Region.builder()
                    .regionName(region)
                    .participations(List.of(participation))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Participations {
        private Long participationId;
        private Long userId;
        private String userName;
        private String locationName;
        private String transportation;

        public static GroupResponse.Participations toParticipateEntity(Participation participation) {
            return Participations.builder()
                    .participationId(participation.getParticipationId())
                    .userId(participation.getUserId())
                    .userName(participation.getUserName())
                    .locationName(participation.getLocationName())
                    .transportation(participation.getTransportation().name())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Participate {
        private Long participationId;
        private Long groupId;
        private Long userId;
        private String userName;
        private String locationName;
        private Double latitude;
        private Double longitude;
        private String transportation;

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Exit {
        private Boolean isDeletedSpace;
        private String message;

        public static GroupResponse.Exit response(Boolean isDeletedSpace, String message) {
            return Exit.builder()
                    .isDeletedSpace(isDeletedSpace)
                    .message(message)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class MyParticipate {
        private Long groupId;
        private String groupName;
        private String groupAdminName;
        private String groupDate;
        private Integer groupParticipates;
        private String confirmPlace;
        private List<String> bestPlaceNames;
        private List<String> participantNames;

        public static GroupResponse.MyParticipate response(
                Groups group, String groupAdminName, List<String> bestPlaceNames, List<String> participantNames
        ) {
            return MyParticipate.builder()
                    .groupId(group.getGroupId())
                    .groupName(group.getName())
                    .groupAdminName(groupAdminName)
                    .groupDate(group.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .groupParticipates(group.getParticipations().size())
                    .confirmPlace(group.getPlace())
                    .bestPlaceNames(bestPlaceNames)
                    .participantNames(participantNames)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Place {
        private String title;
        private String thumUrl;
        private String distance;
        private String openTime;
        private String tel;
        private Detail detail;

        public static Place response(NaverMapListDto.placeList naver, String local) {
            List<String> menuInfo = naver.getMenuInfoOptional()
                    .map(menuInfoString ->
                            Arrays.stream(menuInfoString.split("\\s*\\|\\s*")).collect(Collectors.toList()))
                    .orElseGet(() -> List.of());
            return Place.builder()
                    .title(naver.getName())
                    .thumUrl(naver.getThumUrl())
                    .openTime(naver.getBusinessStatus().getStatus().getDetailInfo())
                    .tel(naver.getTel())
                    .detail(Detail.builder()
                            .local(local)
                            .title(naver.getName())
                            .address(naver.getRoadAddress())
                            .status(naver.getBizhourInfo())
                            .openTime(naver.getBusinessStatus().getStatus().getDetailInfo())
                            .homePageUrl(naver.getHomePage())
                            .tel(naver.getTel())
                            .category(naver.getCategory())
                            .x(naver.getX())
                            .y(naver.getY())
                            .thumUrls(naver.getThumUrls())
                            .menuInfo(menuInfo)
                            .build())
                    .build();
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Builder
        public static class Detail {
            private String local;
            private String title;
            private String address;
            private String status;
            private String openTime;
            private String homePageUrl;
            private String tel;
            private List<String> category;
            private String x;
            private String y;
            private List<String> thumUrls;
            private List<String> menuInfo;
        }
    }
}
