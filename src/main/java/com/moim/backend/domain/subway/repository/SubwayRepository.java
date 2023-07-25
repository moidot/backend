package com.moim.backend.domain.subway.repository;

import com.moim.backend.domain.subway.entity.Subway;
import com.moim.backend.domain.subway.response.BestSubwayInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubwayRepository extends JpaRepository<Subway, Long>, SubwayCustomRepository {

    @Query(value = "select name, latitude, longitude, "
            + "ST_DISTANCE_SPHERE(POINT(longitude, latitude), POINT(:middleLongitude, :middleLatitude)) AS distanceFromMiddlePoint "
            + "from subway "
            + "order by distanceFromMiddlePoint "
            + "limit 10 ", nativeQuery = true)
    List<BestSubwayInterface> getBestSubwayList(
            @Param("middleLatitude") double middleLatitude, @Param("middleLongitude") double middleLongitude
    );

}
