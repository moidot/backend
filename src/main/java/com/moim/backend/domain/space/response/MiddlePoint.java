package com.moim.backend.domain.space.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MiddlePoint {

    private double latitude;
    private double longitude;

    public MiddlePoint(double latitudeSum, double longitudeSum, long participationNum) {
        this.latitude = latitudeSum / participationNum;
        this.longitude = longitudeSum / participationNum;
    }

}
