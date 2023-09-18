package com.moim.backend.global;

import com.moim.backend.global.util.DistanceCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DistanceCalculatorTest {

    private final double startLatitude = 37.4948413;
    private final double startLongitide = 126.8584671;
    private final double endLatitude = 37.7522072;
    private final double endLongitude = 127.0709986;

    @Test
    @DisplayName("위도 경도 거리 계산 구하기")
    void getDistance2() {
        double distance = DistanceCalculator.getDistance(startLatitude, startLongitide, endLatitude, endLongitude);
        System.out.println(distance);
    }

}
