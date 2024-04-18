package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
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
            BestPlace bestPlace,
            List<MoveUserInfo> moveUserInfoList
    ) {
        this.name = bestPlace.getPlaceName();
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
                Space group,
                Participation participation,
                BusGraphicDataResponse busGraphicDataResponse,
                BusPathResponse busPathResponse,
                BestPlace bestPlace
        ) {
            this.isAdmin = (participation.getUserId() == group.getAdminId()) ? true : false;
            this.userId = participation.getUserId();
            this.userName = participation.getUserName();
            this.transportationType = participation.getTransportation();
            this.transitCount = busPathResponse.getTotalTransitCount();
            this.totalTime = busPathResponse.getTotalTime();
            this.totalDistance = busPathResponse.getTotalDistance();
            this.path = busGraphicDataResponse.getPathList(participation, bestPlace);
            this.payment = busPathResponse.getPayment();
        }

        public MoveUserInfo(
                Space group,
                Participation participation,
                CarMoveInfo carMoveInfo,
                BestPlace bestPlace
        ) {
            this.isAdmin = (participation.getUserId() == group.getAdminId()) ? true : false;
            this.userId = participation.getUserId();
            this.userName = participation.getUserName();
            this.transportationType = participation.getTransportation();
            this.totalTime = carMoveInfo.getTotalTime();
            this.totalDistance = carMoveInfo.getTotalDistance();
            this.path = carMoveInfo.getPathList(participation, bestPlace);
            this.payment = carMoveInfo.getPayment();
        }

        public MoveUserInfo(
                Space group,
                Participation participation,
                TmapPublicPathResponse tmapPublicPathResponse,
                BestPlace bestPlace,
                List<PathDto> path
        ) {
            this.isAdmin = (participation.getUserId() == group.getAdminId()) ? true : false;
            this.userId = participation.getUserId();
            this.userName = participation.getUserName();
            this.transportationType = participation.getTransportation();
            this.totalTime = tmapPublicPathResponse.getTotalTime();
            this.totalDistance = tmapPublicPathResponse.getTotalDistance();
            this.path = path;
            this.payment = tmapPublicPathResponse.getPayment();
        }
    }

}
