package com.moim.backend.domain.groupvote.repository;

import com.moim.backend.domain.groupvote.entity.SelectPlace;
import com.moim.backend.domain.groupvote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectPlaceRepository extends JpaRepository<SelectPlace, Long>, SelectPlaceCustomRepository {
    Boolean existsByVoteAndUserId(Vote vote, Long userId);
    int countByVote(Vote vote);
}
