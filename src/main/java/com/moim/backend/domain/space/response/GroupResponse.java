package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
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
        private String groupDate;
        private Integer groupParticipates;
        private List<BestPlaces> bestPlaces;

        public static GroupResponse.MyParticipate response(
                Groups group, List<GroupResponse.BestPlaces> bestPlaces
        ) {
            return MyParticipate.builder()
                    .groupId(group.getGroupId())
                    .groupName(group.getName())
                    .groupDate(group.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .groupParticipates(group.getParticipations().size())
                    .bestPlaces(bestPlaces)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BestPlaces {
        private Long bestPlaceId;
        private String bestPlaceName;

        public static GroupResponse.BestPlaces response(BestPlace bestPlace) {
            return GroupResponse.BestPlaces.builder()
                    .bestPlaceId(bestPlace.getBestPlaceId())
                    .bestPlaceName(bestPlace.getPlaceName())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class detailRecommendedPlace {
        private String placeName;
        private String mainPhotoUrl;
        private String detailPlace;
        private String isOpen;
        private List<KakaoMapDetailDto.TimeList> openTime;
        private String url;
        private String phone;
        private List<String> tags;
        private String delivery;
        private String pagekage;
        private List<Photos> photos;
        private KakaoMapDetailDto.MenuInfo menuInfo;

        public static GroupResponse.detailRecommendedPlace response(KakaoMapDetailDto kakaoMapDetailDto) {
            KakaoMapDetailDto.BasicInfo basicInfo = kakaoMapDetailDto.getBasicInfo();
            KakaoMapDetailDto.Address address = basicInfo.getAddress();
            String placeName = (basicInfo.getPlacenamefull() != null) ? basicInfo.getPlacenamefull() : "none";
            String mainPhotoUrl = (basicInfo.getMainphotourl() != null) ? basicInfo.getMainphotourl() : "none";
            String isOpen = "none";
            List<KakaoMapDetailDto.TimeList> timeList = new ArrayList<>();
            String homePage = (basicInfo.getHomepage() != null) ? basicInfo.getHomepage() : "none";
            String phoneNum = (basicInfo.getPhonenum() != null) ? basicInfo.getPhonenum() : "none";
            List<String> tags = new ArrayList<>();
            String delivery = "none";
            String pagekage = "none";
            List<Photos> photos = new ArrayList<>();
            KakaoMapDetailDto.MenuInfo menuInfo = (kakaoMapDetailDto.getMenuInfo() != null) ?
                    kakaoMapDetailDto.getMenuInfo() : new KakaoMapDetailDto.MenuInfo();

            if (basicInfo.getOpenHour() != null) {
                if (basicInfo.getOpenHour().getRealtime().getOpen() != null) {
                    isOpen = basicInfo.getOpenHour().getRealtime().getOpen().equals("Y") ? "영업중" : "영업종료";
                }
                if (basicInfo.getOpenHour().getRealtime().getCurrentPeriod() != null) {
                    timeList = basicInfo.getOpenHour().getRealtime().getCurrentPeriod().getTimeList();
                }
            }

            if (basicInfo.getTags() != null) {
                tags = basicInfo.getTags();
            }

            if (basicInfo.getOperationInfo() != null && basicInfo.getOperationInfo().getDelivery() != null) {
                delivery = basicInfo.getOperationInfo().getDelivery().equals("Y") ? "배달가능" : "배달불가";
            }

            if (basicInfo.getOperationInfo() != null && basicInfo.getOperationInfo().getPagekage() != null) {
                pagekage = basicInfo.getOperationInfo().getPagekage().equals("Y") ? "포장가능" : "포장불가";
            }

            if (kakaoMapDetailDto.getPhoto() != null) {
                photos = kakaoMapDetailDto.getPhoto().getPhotoList().get(0).getList()
                        .stream()
                        .map(Photos::response)
                        .collect(Collectors.toList());
            }

            return detailRecommendedPlace.builder()
                    .placeName(placeName)
                    .mainPhotoUrl(mainPhotoUrl)
                    .detailPlace(getDetailPlace(address))
                    .isOpen(isOpen)
                    .openTime(timeList)
                    .url(homePage)
                    .phone(phoneNum)
                    .tags(tags)
                    .delivery(delivery)
                    .pagekage(pagekage)
                    .photos(photos)
                    .menuInfo(menuInfo)
                    .build();
        }

        private static String getDetailPlace(KakaoMapDetailDto.Address address) {
            String addFullName = (address.getRegion().getNewaddrfullname() != null) ? address.getRegion().getNewaddrfullname() : "";
            String addMiddleName = (address.getNewaddr().getNewaddrfull() != null) ? address.getNewaddr().getNewaddrfull() : "";
            String addDetail = (address.getAddrdetail() != null) ? address.getAddrdetail() : "";
            return addFullName +
                    " " +
                    addMiddleName +
                    " " +
                    addDetail;
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Photos {
        private String photoId;
        private String photoUrl;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Photos photos = (Photos) o;
            return Objects.equals(getPhotoUrl(), photos.getPhotoUrl());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getPhotoUrl());
        }

        public static GroupResponse.Photos response(KakaoMapDetailDto.PhotoListList photo) {
            return Photos.builder()
                    .photoId(photo.getPhotoid())
                    .photoUrl(photo.getOrgurl())
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
