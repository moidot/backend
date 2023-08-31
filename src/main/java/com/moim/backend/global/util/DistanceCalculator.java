package com.moim.backend.global.util;

public final class DistanceCalculator {

    private static final int EARTH_RADIUS = 6371;
    private static final int KM_TO_M = 1000;

    //두 지점 간의 거리 계산
    public static double getDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        double dLat = Math.toRadians(endLatitude - startLatitude);
        double dLon = Math.toRadians(endLongitude - startLongitude);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(startLatitude)) * Math.cos(Math.toRadians(endLatitude)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c * KM_TO_M;
    }

}
