package com.moim.backend.domain.space.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GraphicDataResponse {
    private Result result;

    public List<Lane> getLane() {
        return result.lane;
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
        private List<GraphPos> graphPos;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphPos {
        @JsonProperty("x")
        private Double longitude;
        @JsonProperty("y")
        private Double latitude;
    }

}
