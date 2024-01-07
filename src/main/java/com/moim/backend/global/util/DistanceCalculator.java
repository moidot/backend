package com.moim.backend.global.util;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;

import java.math.BigDecimal;


public class DistanceCalculator {

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

    public static NumberExpression<Double> calculateDistanceExpression(Double latitude, Double longitude, NumberPath<BigDecimal> latitudeTemplate, NumberPath<BigDecimal> longitudeTemplate) {
        // latitude 를 radians 로 계산
        NumberExpression<Double> radiansLatitude =
                Expressions.numberTemplate(Double.class, "radians({0})", latitude);

        // 계산된 latitude -> 코사인 계산
        NumberExpression<Double> cosLatitude =
                Expressions.numberTemplate(Double.class, "cos({0})", radiansLatitude);
        NumberExpression<Double> cosRadianLatitude =
                Expressions.numberTemplate(Double.class, "cos(radians({0}))", latitudeTemplate);

        // 계산된 latitude -> 사인 계산
        NumberExpression<Double> sinLatitude =
                Expressions.numberTemplate(Double.class, "sin({0})", radiansLatitude);
        NumberExpression<Double> sinRadianLatitude =
                Expressions.numberTemplate(Double.class, "sin(radians({0}))", latitudeTemplate);

        // 사이 거리 계산
        NumberExpression<Double> cosLongitude =
                Expressions.numberTemplate(Double.class, "cos(radians({0}) - radians({1}))", longitudeTemplate, longitude);

        NumberExpression<Double> acosExpression =
                Expressions.numberTemplate(Double.class, "acos({0})", cosLatitude.multiply(cosRadianLatitude).multiply(cosLongitude).add(sinLatitude.multiply(sinRadianLatitude)));

        // 최종 계산
        return Expressions.numberTemplate(Double.class, "6371 * {0}", acosExpression);
    }

}
