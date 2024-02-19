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
import com.moim.backend.domain.user.request.UserDetailCalendarRequest;
import com.moim.backend.domain.user.response.CreateUserCalendarResponse;
import com.moim.backend.domain.user.response.UserCalendarPageResponse;
import com.moim.backend.domain.user.response.UserDetailCalendarResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moim.backend.domain.user.response.UserCalendarPageResponse.Schedule;
import static com.moim.backend.global.util.DateParser.getEndDate;
import static com.moim.backend.global.util.DateParser.getEndDateTime;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.KOREAN;

@Service
@RequiredArgsConstructor
public class UserCalendarService {
    private final UserCalendarRepository userCalendarRepository;
    private final SpaceCalendarRepository spaceCalendarRepository;
    private final ParticipationRepository participationRepository;

    // 개인 캘린더 일정 추가 API
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

    // 캘린더 조회 API
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

    // 해당 날짜 일정 조회 API
    public List<UserDetailCalendarResponse> readDetailCalendar(Users user, UserDetailCalendarRequest request) {
        List<UserDetailCalendarResponse> response = new ArrayList<>();
        List<Participation> participations = participationRepository.findByUserId(user.getUserId());

        // 해당 날짜의 개인 일정 추가
        addMyCalendar(user, request, response);
        // 해당 날짜의 스페이스 일정 추가
        addSpaceCalendar(request, response, participations);

        return response;
    }

    private void addMyCalendar(Users user, UserDetailCalendarRequest request, List<UserDetailCalendarResponse> response) {
        LocalDateTime startDateTime = request.getDate();
        LocalDateTime endDateTime = getEndDateTime(startDateTime);
        List<UserCalendar> userCalendars = userCalendarRepository.findByUserAndDateBetween(user, startDateTime, endDateTime);
        for (UserCalendar userCalendar : userCalendars) {
            response.add(UserDetailCalendarResponse.userCalendarResponse(userCalendar));
        }
    }

    private void addSpaceCalendar(UserDetailCalendarRequest request, List<UserDetailCalendarResponse> response, List<Participation> participations) {
        for (Participation participation : participations) {
            Space space = participation.getSpace();
            LocalDateTime startDateTime = request.getDate();
            LocalDateTime endDateTime = getEndDateTime(startDateTime);

            List<SpaceCalendar> spaceCalendars = spaceCalendarRepository.findBySpaceAndDateBetween(space, startDateTime, endDateTime);
            for (SpaceCalendar spaceCalendar : spaceCalendars) {
                response.add(UserDetailCalendarResponse.spaceCalendarResponse(space, spaceCalendar));
            }
        }
    }

}
