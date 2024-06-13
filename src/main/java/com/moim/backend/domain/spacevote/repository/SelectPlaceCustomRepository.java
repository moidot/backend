package com.moim.backend.domain.spacevote.repository;

import com.moim.backend.domain.space.entity.BestPlace;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectPlaceCustomRepository {
    List<BestPlace> findByVoteStatus(Long spaceId);
    List<Long> findSelectPlaceByUserIdAndVoteId(Long userId, Long voteId);
}
