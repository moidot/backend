package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.response.SpaceFilterEnum;
import com.moim.backend.domain.user.entity.Users;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceCustomRepository {
    List<Space> findBySpaceFetch(Long userId, SpaceFilterEnum filter);
    List<Space> findBySpaceFetch(Long userId, String name, SpaceFilterEnum filter);
    Optional<Space> findBySpaceParticipation(Long groupId);
}
