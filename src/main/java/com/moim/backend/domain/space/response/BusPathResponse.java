package com.moim.backend.domain.space.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BusPathResponse {

    private Result result;
    private Info bestPathInfo;

    public String getPathInfoMapObj() {
        return "0:0@" + searchBestPathInfo().mapObj;
    }

    public int getTotalTransitCount() {
        return searchBestPathInfo().busTransitCount + searchBestPathInfo().subwayTransitCount;
    }

    public int getTotalTime() {
        return searchBestPathInfo().totalTime;
    }

    public Double getTotalDistance() {

        return searchBestPathInfo().totalDistance;
    }
    public int getPayment() {
        return searchBestPathInfo().getPayment();
    }

    private Info searchBestPathInfo() {
        if (bestPathInfo == null) {
            Collections.sort(this.result.path);
            this.bestPathInfo = result.path.get(0).info;
        }

        return bestPathInfo;
    }

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
    public static class Path implements Comparable<Path>{
        private int pathType;
        private Info info;

        @Override
        public int compareTo(Path o) {
            return o.info.getScore() - this.info.getScore();
        }
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

        private int getScore() {
            return busTransitCount + subwayTransitCount + totalTime;
        }
    }

}
