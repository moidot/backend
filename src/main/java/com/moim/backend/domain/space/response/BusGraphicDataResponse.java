package com.moim.backend.domain.space.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public List<PathDto> getPathList() {
        List<PathDto> path = new ArrayList<>();
        result.lane.get(0).section.forEach(section -> {
            path.addAll(section.graphPos);
        });
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
