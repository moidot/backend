package com.moim.backend.domain.user.repository;

import com.moim.backend.domain.user.entity.UserCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCalendarRepository extends JpaRepository<UserCalendar, Long> {
}
