package com.moim.backend.domain.hotplace.repository;

import com.moim.backend.domain.hotplace.entity.HotPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotPlaceRepository extends JpaRepository<HotPlace, Long>, HotPlaceCustomRepository {
}
