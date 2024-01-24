package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.entity.UserCalendar;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserCalendarRepository;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
import com.moim.backend.domain.user.response.CreateUserCalendarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.KOREAN;

@Service
@RequiredArgsConstructor
public class UserCalendarService {
    private final UserCalendarRepository userCalendarRepository;

    public CreateUserCalendarResponse createUserCalendar(Users user, CreateUserCalendarRequest request) {
        UserCalendar userCalendar = userCalendarRepository.save(
                toUserCalendarEntity(user, request)
        );
        return CreateUserCalendarResponse.response(userCalendar);
    }

    private static UserCalendar toUserCalendarEntity(Users user, CreateUserCalendarRequest request) {
        return UserCalendar.builder()
                .user(user)
                .scheduleName(request.getScheduleName())
                .date(request.getDate())
                .dayOfWeek(request.getDate().getDayOfWeek().getDisplayName(SHORT, KOREAN))
                .note(request.getNote())
                .locationName(request.getLocationName())
                .color(request.getColor())
                .build();
    }
}
