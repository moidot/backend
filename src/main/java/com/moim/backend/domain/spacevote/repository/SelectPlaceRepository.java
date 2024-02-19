package com.moim.backend.domain.spacevote.repository;

import com.moim.backend.domain.spacevote.entity.SelectPlace;
import com.moim.backend.domain.spacevote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectPlaceRepository extends JpaRepository<SelectPlace, Long>, SelectPlaceCustomRepository {
    Boolean existsByVoteAndUserId(Vote vote, Long userId);
    int countByVote(Vote vote);
    void deleteByVote(Vote vote);
}
