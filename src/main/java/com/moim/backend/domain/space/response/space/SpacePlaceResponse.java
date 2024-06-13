package com.moim.backend.domain.space.response.space;

import com.moim.backend.domain.space.response.NaverMapListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SpacePlaceResponse {
    private String title;
    private String thumUrl;
    private String distance;
    private String openTime;
    private String tel;
    private Detail detail;

    public static SpacePlaceResponse response(NaverMapListDto.placeList naver, String local) {
        List<String> menuInfo = naver.getMenuInfoOptional()
                .map(menuInfoString ->
                        Arrays.stream(menuInfoString.split("\\s*\\|\\s*")).collect(Collectors.toList()))
                .orElseGet(() -> List.of());
        return SpacePlaceResponse.builder()
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
