package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.SpaceCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SpaceCalendarRepository extends JpaRepository<SpaceCalendar, Long> {
    List<SpaceCalendar> findBySpaceAndDateBetween(Space space, LocalDateTime startDate, LocalDateTime endDate);
}
