package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.subway.response.BestPlaceInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRouteResponse {

    private String name;
    private Double latitude;
    private Double longitude;
    private List<MoveUserInfo> moveUserInfo = new ArrayList<>();

    public PlaceRouteResponse(
            BestPlaceInterface bestPlace,
            List<MoveUserInfo> moveUserInfoList
    ) {
        this.name = bestPlace.getName();
        this.latitude = bestPlace.getLatitude();
        this.longitude = bestPlace.getLongitude();
        this.moveUserInfo = moveUserInfoList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoveUserInfo {
        private Long userId;
        private String userName;
        private TransportationType transportationType;
        private int transitCount; // 총 환승 횟수
        private int totalTime; // 단위: 분(m)
        private Double totalDistance;
        private List<PathDto> path;

        public MoveUserInfo(
                Participation participation,
                BusGraphicDataResponse busGraphicDataResponse,
                BusPathResponse busPathResponse
        ) {
            this.userId = participation.getUserId();
            this.userName = participation.getUserName();
            this.transportationType = participation.getTransportation();
            this.transitCount = busPathResponse.getTotalTransitCount();
            this.totalTime = busPathResponse.getTotalTime();
            this.totalDistance = busPathResponse.getTotalDistance();
            this.path = busGraphicDataResponse.getPathList(participation);
        }

        public MoveUserInfo(
                Participation participation,
                CarMoveInfo carMoveInfo
        ) {
            this.userId = participation.getUserId();
            this.userName = participation.getUserName();
            this.transportationType = participation.getTransportation();
            this.totalTime = carMoveInfo.getTotalTime();
            this.totalDistance = carMoveInfo.getTotalDistance();
            this.path = carMoveInfo.getPathList(participation);
        }
    }

}
