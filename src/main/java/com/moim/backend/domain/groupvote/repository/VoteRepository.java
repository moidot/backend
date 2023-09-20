package com.moim.backend.domain.groupvote.repository;

import com.moim.backend.domain.groupvote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByGroupId (Long groupId);
    void deleteByGroupId(Long groupId);
}
