package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Space;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceCustomRepository {
    List<Space> findBySpaceFetch(Long userId);
    Optional<Space> findBySpaceParticipation(Long groupId);
}
