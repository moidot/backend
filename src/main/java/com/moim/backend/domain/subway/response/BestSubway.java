package com.moim.backend.domain.subway.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BestSubway implements BestSubwayInterface {

    private String name;
    private double latitude;
    private double longitude;
    private double distanceFromMiddlePoint;

    @Builder
    public BestSubway(String name, double latitude, double longitude, double distanceFromMiddlePoint) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceFromMiddlePoint = distanceFromMiddlePoint;
    }

}
