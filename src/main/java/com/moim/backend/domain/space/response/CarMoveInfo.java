package com.moim.backend.domain.space.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moim.backend.domain.space.entity.Participation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CarMoveInfo implements MoveInfoInterface, PathGraphicDataInterface {
    private List<Route> routes;

    private Route getBestRoute() {
        return routes.get(0);
    }

    private Summary getBestRouteSummary() {
        return getBestRoute().summary;
    }

    private Section getBestRouteSection() {
        return getBestRoute().getSections().get(0);
    }

    private void addVertexToPathList(Double[] vertexes, List<PathDto> path) {
        int lastEvenIdx = vertexes.length - 2;
        IntStream.range(0, lastEvenIdx / 2).forEach(idx -> {
            path.add(PathDto.builder()
                    .longitude(vertexes[idx * 2])
                    .latitude(vertexes[idx * 2 + 1])
                    .build());
        });
    }

    @Override
    public int getTotalTime() {
        return getBestRouteSummary().duration;
    }

    @Override
    public Double getTotalDistance() {
        return (double) getBestRouteSummary().distance;
    }

    @Override
    public List<PathDto> getPathList(Participation participation) {
        // 위도, 경도 구분 없이 배열로 존재하여 vertexes를 Path 리스트로 변환
        // 짝수 인덱스: 경도, 홀스 인덱스: 위도
        List<PathDto> path = new ArrayList<>();
        path.add(PathDto.builder().latitude(participation.getLatitude()).longitude(participation.getLongitude()).build());
        getBestRouteSection().roads.stream().forEach(road -> addVertexToPathList(road.vertexes, path));
        return path;
    }

    @Getter
    @NoArgsConstructor
    private static class Route {
        @JsonProperty("result_code")
        public int resultCode;
        public Summary summary;
        public List<Section> sections;
    }

    @Getter
    @NoArgsConstructor
    private static class Summary {
        public Fare fare;
        public int distance;
        public int duration;
    }

    @Getter
    @NoArgsConstructor
    private static class Fare {
        public int taxi;
        public int toll;
    }

    @Getter
    @NoArgsConstructor
    private static class Section {
        public List<Road> roads;
    }

    @Getter
    @NoArgsConstructor
    private static class Road {
        public String name;
        public Double[] vertexes;
    }

}
