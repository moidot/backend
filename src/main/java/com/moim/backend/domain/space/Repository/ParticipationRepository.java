package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.response.MiddlePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("SELECT "
            + "new com.moim.backend.domain.space.response.MiddlePoint(SUM(p.latitude), SUM(p.longitude), COUNT(*)) "
            + "FROM Participation p "
            + "GROUP BY p.group.groupId "
            + "HAVING p.group.groupId = :groupId")
    MiddlePoint getMiddlePoint(@Param("groupId") Long groupId);

}
