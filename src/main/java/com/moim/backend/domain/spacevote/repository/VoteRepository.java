package com.moim.backend.domain.spacevote.repository;

import com.moim.backend.domain.spacevote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findBySpaceId (Long spaceId);
    void deleteBySpaceId(Long spaceId);
}
