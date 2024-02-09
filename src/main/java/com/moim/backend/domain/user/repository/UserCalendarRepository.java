package com.moim.backend.domain.user.repository;

import com.moim.backend.domain.user.entity.UserCalendar;
import com.moim.backend.domain.user.entity.Users;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserCalendarRepository extends JpaRepository<UserCalendar, Long> {
    List<UserCalendar> findByUserAndDateBetween(Users user, LocalDateTime startDate, LocalDateTime endDate);
}
