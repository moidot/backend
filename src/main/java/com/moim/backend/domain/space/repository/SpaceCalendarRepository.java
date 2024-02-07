package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.SpaceCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceCalendarRepository extends JpaRepository<SpaceCalendar, Long> {
}
