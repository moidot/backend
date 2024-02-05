package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.response.MiddlePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    @Query("SELECT "
            + "new com.moim.backend.domain.space.response.MiddlePoint(SUM(p.latitude), SUM(p.longitude), COUNT(*)) "
            + "FROM Participation p "
            + "GROUP BY p.space "
            + "HAVING p.space = :space")
    MiddlePoint getMiddlePoint(@Param("space") Space space);

    int countBySpaceAndUserId(Space space, Long userId);

    List<Participation> findAllBySpaceSpaceIdAndUserIdIn(Long spaceId, List<Long> userId);
    List<Participation> findAllBySpace(Space space);
    List<Participation> findAllBySpaceAndUserName(Space space, String name);

    List<Participation> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
