package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Groups;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupCustomRepository {
    Optional<Groups> findByIdToFetchJoinBestPlace(Long groupId);
    List<Groups> myParticipationGroups(Long userId);
    Optional<Groups> findByGroupParticipation(Long groupId);
}
