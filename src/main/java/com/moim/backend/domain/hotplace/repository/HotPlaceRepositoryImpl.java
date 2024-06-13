package com.moim.backend.domain.hotplace.repository;

import com.moim.backend.domain.hotplace.entity.HotPlace;
import com.moim.backend.global.dto.BestRegion;
import com.moim.backend.global.util.DistanceCalculator;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.moim.backend.domain.hotplace.entity.QHotPlace.hotPlace;

public class HotPlaceRepositoryImpl implements HotPlaceCustomRepository {

    private final JPAQueryFactory queryFactory;

    public HotPlaceRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public List<BestRegion> getNearestHotPlaceList(Double latitude, Double longitude, Double validRange) {
        NumberExpression<Double> distanceExpression = DistanceCalculator.calculateDistanceExpression(latitude, longitude, hotPlace.latitude, hotPlace.longitude);

        JPAQuery<HotPlace> query = queryFactory.selectFrom(hotPlace)
                .orderBy(distanceExpression.asc())
                .limit(10);

        if (validRange != null) {
            query.where(distanceExpression.loe(validRange));
        }

        List<HotPlace> findByNearestStationList = query.fetch();

        return findByNearestStationList.stream()
                .distinct()
                .limit(3)
                .map(BestRegion::new)
                .collect(Collectors.toList());
    }
}
