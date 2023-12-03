package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BestPlaceRepository extends JpaRepository<BestPlace, Long> {

    int deleteAllByGroup(Groups group);
    List<BestPlace> findAllByGroup(Groups group);

}
