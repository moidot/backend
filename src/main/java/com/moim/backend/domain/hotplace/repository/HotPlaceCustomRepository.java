package com.moim.backend.domain.hotplace.repository;

import com.moim.backend.global.dto.BestRegion;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotPlaceCustomRepository {
    List<BestRegion> getNearestHotPlaceList(Double latitude, Double longitude, Double validRange);
}
