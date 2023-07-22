package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Groups;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupCustomRepository {
    List<Groups> myParticipationGroups(Long userId);
}
