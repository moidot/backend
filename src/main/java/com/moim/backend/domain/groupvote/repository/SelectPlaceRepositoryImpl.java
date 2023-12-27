package com.moim.backend.domain.groupvote.repository;

import com.moim.backend.domain.groupvote.entity.QSelectPlace;
import com.moim.backend.domain.space.entity.BestPlace;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.moim.backend.domain.groupvote.entity.QSelectPlace.selectPlace;
import static com.moim.backend.domain.space.entity.QBestPlace.*;

@RequiredArgsConstructor
public class SelectPlaceRepositoryImpl implements SelectPlaceCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BestPlace> findByVoteStatus(Long groupId) {
        return queryFactory
                .selectFrom(bestPlace)
                .leftJoin(bestPlace.selectPlaces, selectPlace)
                .where(bestPlace.group.groupId.eq(groupId))
                .fetchJoin()
                .fetch();
    }

    @Override
    public List<Long> findSelectPlaceByUserIdAndVoteId(Long userId, Long voteId) {

        return queryFactory
                .select(selectPlace.selectPlaceId)
                .from(selectPlace)
                .where(
                        selectPlace.userId.eq(userId).and(selectPlace.vote.voteId.eq(voteId))
                )
                .fetch();
    }
}
