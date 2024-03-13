package com.moim.backend.domain.space.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Participation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BusGraphicDataResponse implements PathGraphicDataInterface {
    private Result result;

    public List<Lane> getLane() {
        return result.lane;
    }

    @Override
    public List<PathDto> getPathList(Participation participation, BestPlace bestPlace) {
        List<PathDto> path = new ArrayList<>();
        path.add(PathDto.builder().latitude(participation.getLatitude()).longitude(participation.getLongitude()).build());
        for (Lane lane : result.lane) {
            for (Section section : lane.section) {
                path.addAll(section.graphPos);
            }
        }
        path.add(new PathDto(bestPlace.getLongitude(), bestPlace.getLatitude()));
        return path;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private List<Lane> lane;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lane {
        @JsonProperty("class")
        private int laneClass; // 1(버스노선), 2(지하철노선)
        private List<Section> section;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Section {
        private List<PathDto> graphPos;

    }

}
