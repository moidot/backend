package com.moim.backend.domain.space.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchPathResponse {

    private Result result;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private int searchType;
        private int outTrafficCheck;
        private int busCount;
        private int subwayCount;
        private List<Path> path;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Path {
        private int pathType;
        private Info info;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private Double trafficDistance; // 도보를 제외한 총 이동 거리
        private int totalWalk; // 도보 이동 거리
        private int totalTime;
        private int payment;
        private int busTransitCount; // 버스 환승 횟수
        private int subwayTransitCount; // 지하철 환승 횟수
        private String mapObj; // 경로를 가져오기 위해 필요한 데이터
        private String firstStartStation; // 최초 출발역 / 정류장
        private String lastEndStation; // 최종 도착역 / 정류장
        private int totalStationCount;
        private int busStationCount;
        private int subwayStationCount;
        private Double totalDistance;
        private int checkIntervalTime;
        private String checkIntervalTimeOverYn;
    }

    public String getPathInfoMapObj() {
        return "0:0@" + result.path.get(0).info.mapObj;
    }

    public int getTotalTransitCount() {
        return result.path.get(0).info.busTransitCount + result.path.get(0).info.subwayTransitCount;
    }

    public int getTotalTime() {
        return result.path.get(0).info.totalTime;
    }

    public Double getTotalDistance() {
        return result.path.get(0).info.totalDistance;
    }
}
