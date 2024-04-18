package com.moim.backend.domain.space.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TmapPublicPathResponse implements MoveInfoInterface {

    private MetaData metaData;

    @Override
    public int getTotalTime() {
        return getBestPath().getTotalTime() / 60;
    }

    @Override
    public Double getTotalDistance() {
        return (double) getBestPath().getTotalDistance();
    }

    @Override
    public int getPayment() {
        return getBestPath().getFare().getRegular().getTotalFare();
    }

    public List<Leg> getPathList() {
        return getBestPath().legs;
    }

    private Itinerary getBestPath() {
        Collections.sort(metaData.plan.itineraries);
        return metaData.plan.itineraries.get(0);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class MetaData {
        private Plan plan;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Plan {
        private List<Itinerary> itineraries;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Itinerary implements Comparable<Itinerary> {
        private Fare fare;
        private int totalTime; // 총 소요 시간. 단위: 초
        private int totalDistance; // 총 거리. 단위: m
        private int transferCount; // 환승 횟수
        private int totalWalkDistance; // 총 도보 거리. 단위: m
        private int totalWalkTime; // 총 도보 소요 시간. 단위: 초
        private List<Leg> legs; // 경로들 리스트

        // 치환된 점수 의미는 피로도라고 볼 수 있음
        // 우선순위: 치환된 점수가 낮을수록 -> 환승 적을수록 -> 도보 적을 수록
        // 현재 객체가 넘어오는 객체보다 클 때 1을 반환한다면, 해당 값을 기준으로 오름차순
        @Override
        public int compareTo(Itinerary o) {
            if (this.calculateScore() > o.calculateScore()) {
                return 1;
            } else if (this.calculateScore() == o.calculateScore()) {
                return compareToTransferCount(o);
            } else {
                return -1;
            }
        }

        private int compareToTransferCount(Itinerary o) {
            if (this.transferCount > o.transferCount) {
                return 1;
            } else if (this.transferCount == o.transferCount) {
                return compareToTotalWalkTime(o);
            } else {
                return -1;
            }
        }

        private int compareToTotalWalkTime(Itinerary o) {
            if (this.totalWalkTime > o.totalWalkDistance) {
                return 1;
            } else if (this.totalWalkDistance == o.totalWalkDistance) {
                return 0;
            } else {
                return -1;
            }
        }

        // 최적의 경로를 선택하기 위해 이동 방법을 점수로 치환
        // 예시 경로 1: 총 시간 50분, 환승 0번, 도보 10분 => 50 + 00 + 30 = 80
        // 예시 경로 2: 총 시간 40분, 환승 1번, 도보 10분 => 40 + 10 + 30 = 80
        // 예시 경로 3: 총 시간 30분, 환승 1번, 도보 20분 => 30 + 10 + 60 = 100
        // 예시 경로 4: 총 시간 30분, 환승 0번, 도보 30분 => 30 + 00 + 90 = 120
        // 예시 경로 5: 총 시간 20분, 환승 3번, 도보 10분 => 20 + 30 + 30 = 80
        private int calculateScore() {
            return totalTime + (transferCount * 10) + (totalWalkTime * 3);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Fare {
        private Regular regular;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Regular {
        private int totalFare;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Leg {
        private String mode;
        private List<Step> steps;
        private PassStop passStopList;

        public List<Station> getStationList() {
            if (passStopList == null) {
                return null;
            }
            return passStopList.stationList;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {
        private String linestring; // 위도, 경도 리스트. 예시: 127.06563,37.652164 127.06558,37.65231
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassStop {
        private List<Station> stationList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Station {
        private String stationName;
        private String lon;
        private String lat;
    }
}
