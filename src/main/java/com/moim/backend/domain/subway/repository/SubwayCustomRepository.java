package com.moim.backend.domain.subway.repository;

import com.moim.backend.domain.subway.entity.Subway;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubwayCustomRepository {
    List<Subway> getNearestStationsList(Double latitude, Double longitude);
}
