package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long>, SpaceCustomRepository {
}
