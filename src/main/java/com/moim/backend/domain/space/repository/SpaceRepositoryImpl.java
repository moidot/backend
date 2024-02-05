package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Space;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.moim.backend.domain.space.entity.QBestPlace.bestPlace;
import static com.moim.backend.domain.space.entity.QSpace.space;
import static com.moim.backend.domain.space.entity.QParticipation.participation;

@Repository
public class SpaceRepositoryImpl implements SpaceCustomRepository {
    private final JPAQueryFactory queryFactory;

    public SpaceRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Space> findBySpaceFetch(Long userId) {
        return queryFactory
                .selectFrom(space)
                .innerJoin(space.participations, participation)
                .leftJoin(space.bestPlaces, bestPlace)
                .where(participation.userId.eq(userId))
                .fetchJoin()
                .fetch();
    }

    @Override
    public Optional<Space> findBySpaceParticipation(Long spaceId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(space)
                .leftJoin(space.participations, participation).fetchJoin()
                .where(space.spaceId.eq(spaceId))
                .fetchOne()
        );
    }
}
