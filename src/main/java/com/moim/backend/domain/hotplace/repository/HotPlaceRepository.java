package com.moim.backend.domain.hotplace.repository;

import com.moim.backend.domain.hotplace.entity.HotPlace;
import com.moim.backend.domain.subway.response.BestPlaceInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotPlaceRepository extends JpaRepository<HotPlace, Long>, HotPlaceCustomRepository {

    @Query(value = "select name, latitude, longitude, "
            + "ST_DISTANCE_SPHERE(POINT(longitude, latitude), POINT(:middleLongitude, :middleLatitude)) AS distanceFromMiddlePoint "
            + "from hot_place "
            + "having distanceFromMiddlePoint <= :validRange "
            + "order by distanceFromMiddlePoint "
            + "limit 3 ", nativeQuery = true)
    List<BestPlaceInterface> getBestHotPlaceList(
            @Param("middleLatitude") double middleLatit록ude, @Param("middleLongitude") double middleLongitude, @Param("validRange") double validRange
    );

}
