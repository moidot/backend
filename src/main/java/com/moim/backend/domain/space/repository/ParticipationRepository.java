package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Groups;
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
            + "GROUP BY p.group "
            + "HAVING p.group = :group")
    MiddlePoint getMiddlePoint(@Param("group") Groups group);

    Participation findByGroupAndUserId(Groups group, Long userId);
    int countByGroupAndUserId(Groups group, Long userId);

}
