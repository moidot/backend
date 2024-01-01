package com.moim.backend.domain.subway.repository;

import com.moim.backend.global.dto.BestRegion;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubwayCustomRepository {
    List<BestRegion> getNearestStationsList(Double latitude, Double longitude);
    List<BestRegion> getNearestStationsList(Double latitude, Double longitude, Double validRange);
}
