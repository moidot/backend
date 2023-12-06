package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Groups;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupCustomRepository {
    List<Groups> findByGroupsFetch(Long userId);
    Optional<Groups> findByGroupParticipation(Long groupId);
}
