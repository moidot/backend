package com.moim.backend.domain.subway.repository;

import com.moim.backend.domain.subway.entity.Subway;
import com.moim.backend.global.dto.BestRegion;
import com.moim.backend.global.util.DistanceCalculator;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.moim.backend.domain.subway.entity.QSubway.subway;

public class SubwayRepositoryImpl implements SubwayCustomRepository {

    private final JPAQueryFactory queryFactory;

    public SubwayRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public List<BestRegion> getNearestStationsList(Double latitude, Double longitude) {
        return getNearestStationsList(latitude, longitude, null);
    }

    @Override
    public List<BestRegion> getNearestStationsList(Double latitude, Double longitude, Double validRange) {
        NumberExpression<Double> distanceExpression = DistanceCalculator.calculateDistanceExpression(latitude, longitude, subway.latitude, subway.longitude);

        JPAQuery<Subway> query = queryFactory.selectFrom(subway)
                .orderBy(distanceExpression.asc())
                .limit(10);

        if (validRange != null) {
            query.where(distanceExpression.loe(validRange));
        }

        List<Subway> findByNearestStationList = query.fetch();

        return findByNearestStationList.stream()
                .distinct()
                .limit(3)
                .map(BestRegion::new)
                .collect(Collectors.toList());
    }
}
