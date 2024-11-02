package com.moim.backend.domain.space.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TmapWalkPathResponse  {
    public List<Feature> features;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Feature {
        public Geometry geometry;
        public Properties properties;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Geometry {
        public String type;
        public String coordinates;
    }

    // Geometry 타입에 따라 시간이나 거리를 넘겨줄 때의 키 값이 달라짐
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties {
        private long totalDistance; // 단위: m. Geometry 타입이 "Point"일 때
        private long totalTime; // 단위: 초. Geometry 타입이 "Point"일 때
        private long distance; // 단위: m. Geometry 타입이 "LineString"일 때
        private long time; // 단위: 초. Geometry 타입이 "LineString"일 때

        public long getTotalTime() {
            return totalTime + time;
        }

        public long getTotalDistance() {
            return totalDistance + distance;
        }
    }
}
