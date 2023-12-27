package com.moim.backend.domain.groupvote.repository;

import com.moim.backend.domain.space.entity.BestPlace;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SelectPlaceCustomRepository {
    List<BestPlace> findByVoteStatus(Long groupId);
    List<Long> findSelectPlaceByUserIdAndVoteId(Long userId, Long voteId);
}
