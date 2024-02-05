package com.moim.backend.domain.spacevote.repository;

import com.moim.backend.domain.space.entity.BestPlace;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.moim.backend.domain.spacevote.entity.QSelectPlace.selectPlace;
import static com.moim.backend.domain.space.entity.QBestPlace.*;

@RequiredArgsConstructor
public class SelectPlaceRepositoryImpl implements SelectPlaceCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BestPlace> findByVoteStatus(Long spaceId) {
        return queryFactory
                .selectFrom(bestPlace)
                .leftJoin(bestPlace.selectPlaces, selectPlace)
                .where(bestPlace.space.spaceId.eq(spaceId))
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
