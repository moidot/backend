package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.QBestPlace;
import com.moim.backend.domain.space.entity.QParticipation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.moim.backend.domain.space.entity.QBestPlace.bestPlace;
import static com.moim.backend.domain.space.entity.QGroups.groups;
import static com.moim.backend.domain.space.entity.QParticipation.participation;

@Repository
public class GroupRepositoryImpl implements GroupCustomRepository {
    private final JPAQueryFactory queryFactory;

    public GroupRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Groups> findByIdToFetchJoinBestPlace(Long groupId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(groups)
                .leftJoin(groups.bestPlaces, bestPlace)
                .where(groups.groupId.eq(groupId))
                .fetchJoin()
                .fetchOne());
    }

    @Override
    public List<Groups> myParticipationGroups(Long userId) {
        return queryFactory
                .selectFrom(groups)
                .innerJoin(groups.participations, participation)
                .leftJoin(groups.bestPlaces, bestPlace)
                .where(participation.userId.eq(userId))
                .fetchJoin()
                .fetch();
    }

    @Override
    public Optional<Groups> findByGroupParticipation(Long groupId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(groups)
                .leftJoin(groups.participations, participation).fetchJoin()
                .where(groups.groupId.eq(groupId))
                .fetchOne()
        );
    }
}
