package com.moim.backend.domain.groupvote.repository;

import com.moim.backend.domain.groupvote.entity.SelectPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectPlaceRepository extends JpaRepository<SelectPlace, Long>, SelectPlaceCustomRepository {
}
