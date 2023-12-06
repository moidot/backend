package com.moim.backend.domain.subway.repository;

import com.moim.backend.domain.subway.entity.Subway;
import com.moim.backend.domain.subway.response.BestPlaceInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubwayRepository extends JpaRepository<Subway, Long>, SubwayCustomRepository {

    @Query(value = "select CONCAT(name, '역') as name, latitude, longitude, "
            + "ST_DISTANCE_SPHERE(POINT(longitude, latitude), POINT(:middleLongitude, :middleLatitude)) AS distanceFromMiddlePoint "
            + "from subway "
            + "having distanceFromMiddlePoint <= :validRange "
            + "order by distanceFromMiddlePoint "
            + "limit 3 ", nativeQuery = true)
    List<BestPlaceInterface> getBestSubwayList( // TODO: 이름이 같은 지하철역은 더 가까운 게 한개만 반환되도록
            @Param("middleLatitude") double middleLatitude, @Param("middleLongitude") double middleLongitude, @Param("validRange") double validRange
    );

}
