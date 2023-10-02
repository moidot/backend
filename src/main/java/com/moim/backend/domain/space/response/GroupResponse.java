package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.user.entity.Users;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.*;

public class GroupResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = PRIVATE)
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
                    .groupDate(group.getDate()
                            .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .orElse("none"))
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
