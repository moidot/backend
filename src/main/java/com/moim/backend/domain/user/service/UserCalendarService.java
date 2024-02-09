package com.moim.backend.domain.user.service;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.SpaceCalendar;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.repository.SpaceCalendarRepository;
import com.moim.backend.domain.user.entity.UserCalendar;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserCalendarRepository;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
import com.moim.backend.domain.user.request.UserCalendarPageRequest;
import com.moim.backend.domain.user.response.CreateUserCalendarResponse;
import com.moim.backend.domain.user.response.UserCalendarPageResponse;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moim.backend.domain.user.response.UserCalendarPageResponse.*;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.KOREAN;

@Service
@RequiredArgsConstructor
public class UserCalendarService {
    private final UserCalendarRepository userCalendarRepository;
    private final SpaceCalendarRepository spaceCalendarRepository;
    private final ParticipationRepository participationRepository;

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

    public UserCalendarPageResponse readMyCalendar(Users user, @Valid UserCalendarPageRequest request) {
        // 기본 1~31 까지 응답 값 생성
        Map<Integer, List<Schedule>> data = getDefaultResponse();
        LocalDateTime startDate = request.getDate();
        LocalDateTime endDate = getEndDate(startDate);

        // 개인 일정 캘린더에 등록 (color, schedule)
        List<UserCalendar> userCalendars = userCalendarRepository.findByUserAndDateBetween(user, startDate, endDate);
        addMyDate(data, userCalendars);

        // 스페이스 일정 캘린더에 등록 (color, schedule)
        List<Participation> participations = participationRepository.findByUserId(user.getUserId());
        addSpaceDate(data, startDate, endDate, participations);

        return UserCalendarPageResponse.response(data);
    }

    private void addSpaceDate(Map<Integer, List<Schedule>> data, LocalDateTime startDate, LocalDateTime endDate, List<Participation> participations) {
        for (Participation participation : participations) {
            Space space = participation.getSpace();
            List<SpaceCalendar> spaceCalendars = spaceCalendarRepository.findBySpaceAndDateBetween(space, startDate, endDate);
            for (SpaceCalendar spaceCalendar : spaceCalendars) {
                for (Integer day : data.keySet()) {
                    if (spaceCalendar.getDate().getDayOfMonth() == day) {
                        List<Schedule> schedules = data.get(day);
                        schedules.add(Schedule.builder()
                                .color("space")
                                .scheduleName(spaceCalendar.getScheduleName())
                                .build());
                        data.put(day, schedules);
                    }
                }
            }
        }
    }

    private static void addMyDate(Map<Integer, List<Schedule>> data, List<UserCalendar> userCalendars) {
        for (UserCalendar userCalendar : userCalendars) {
            for (Integer day : data.keySet()) {
                if (userCalendar.getDate().getDayOfMonth() == day) {
                    List<Schedule> schedules = data.get(day);
                    schedules.add(Schedule.builder()
                            .color(userCalendar.getColor())
                            .scheduleName(userCalendar.getScheduleName())
                            .build());
                    data.put(day, schedules);
                }
            }
        }
    }

    private static Map<Integer, List<Schedule>> getDefaultResponse() {
        Map<Integer, List<Schedule>> response = new HashMap<>();
        for (int day = 1; day <= 31; day++) {
            response.put(day, new ArrayList<>());
        }
        return response;
    }

    private List<Participation> getParticipations(Users user) {
        return participationRepository.findByUserId(user.getUserId());
    }

    private static LocalDateTime getEndDate(LocalDateTime startDate) {
        YearMonth yearMonth = YearMonth.from(startDate);
        return yearMonth.atEndOfMonth().atTime(23, 59, 59);
    }

}
