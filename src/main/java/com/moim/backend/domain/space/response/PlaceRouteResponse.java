package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.Groups;
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
        private Boolean isAdmin;
        private Long userId;
        private String userName;
        private TransportationType transportationType;
        private int transitCount; // 총 환승 횟수
        private int totalTime; // 단위: 분(m)
        private Double totalDistance;
        private int payment;
        private List<PathDto> path;

        public MoveUserInfo(
                Groups group,
                Participation participation,
                BusGraphicDataResponse busGraphicDataResponse,
                BusPathResponse busPathResponse
        ) {
            this.isAdmin = (participation.getUserId() == group.getAdminId()) ? true : false;
            this.userId = participation.getUserId();
            this.userName = participation.getUserName();
            this.transportationType = participation.getTransportation();
            this.transitCount = busPathResponse.getTotalTransitCount();
            this.totalTime = busPathResponse.getTotalTime();
            this.totalDistance = busPathResponse.getTotalDistance();
            this.path = busGraphicDataResponse.getPathList(participation);
            this.payment = busPathResponse.getPayment();
        }

        public MoveUserInfo(
                Groups group,
                Participation participation,
                CarMoveInfo carMoveInfo
        ) {
            this.isAdmin = (participation.getUserId() == group.getAdminId()) ? true : false;
            this.userId = participation.getUserId();
            this.userName = participation.getUserName();
            this.transportationType = participation.getTransportation();
            this.totalTime = carMoveInfo.getTotalTime();
            this.totalDistance = carMoveInfo.getTotalDistance();
            this.path = carMoveInfo.getPathList(participation);
            this.payment = carMoveInfo.getPayment();
        }
    }

}
