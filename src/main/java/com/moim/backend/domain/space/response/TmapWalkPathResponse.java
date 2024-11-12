package com.moim.backend.domain.space.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TmapWalkPathResponse implements MoveInfoInterface {
    public List<Feature> features;

    @Override
    public int getTotalTime() {
        return Math.round(features.get(0).properties.getTotalTime() / 60);
    }

    @Override
    public Double getTotalDistance() {
        return Double.valueOf(features.get(0).properties.getDistance());
    }

    @Override
    public int getPayment() {
        return 0;
    }

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
        public List<Object> coordinates;
    }

    // Geometry 타입에 따라 시간이나 거리를 넘겨줄 때의 키 값이 달라짐
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties {
        // totalDistance는 여러 feature 중 한 개에만 담겨서 옴
        // 가장 첫 번째 feature에 담겨서 오는 듯 하나 항상 그런지는 모름.
        private long totalDistance; // 단위: m
        private long totalTime; // 단위: 초
        private long distance; // 단위: m
        private long time; // 단위: 초
    }
}
