package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.subway.response.BestSubwayInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PlaceRouteResponse {

    private String name;
    private Double latitude;
    private Double longitude;
    private List<MoveUserInfo> moveUserInfo = new ArrayList<>();

    public PlaceRouteResponse(BestSubwayInterface bestSubway) {
        this.name = bestSubway.getName();
        this.latitude = bestSubway.getLatitude();
        this.longitude = bestSubway.getLongitude();
    }

    public void addMoveUserInfo(MoveUserInfo moveUserInfo) {
        this.moveUserInfo.add(moveUserInfo);
    }

    @Getter
    @NoArgsConstructor
    public static class MoveUserInfo {
        private Long userId;
        private String userName;
        private int transitCount; // 총 환승 횟수
        private int totalTime; // 단위: 분(m)
        private Double totalDistance;
        private List<GraphicDataResponse.Lane> lane;

        public MoveUserInfo(
                Participation participation,
                GraphicDataResponse graphicDataResponse,
                SearchPathResponse searchPathResponse
        ) {
            this.userId = participation.getUserId();
            this.userName = participation.getUserName();
            this.transitCount = searchPathResponse.getTotalTransitCount();
            this.totalTime = searchPathResponse.getTotalTime();
            this.totalDistance = searchPathResponse.getTotalDistance();
            this.lane = graphicDataResponse.getLane();
        }
    }

}
