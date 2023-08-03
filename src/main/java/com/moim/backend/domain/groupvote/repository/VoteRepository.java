package com.moim.backend.domain.groupvote.repository;

import com.moim.backend.domain.groupvote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
}
