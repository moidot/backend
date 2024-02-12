package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.SpaceCalendar;
import com.moim.backend.domain.space.repository.SpaceCalendarRepository;
import com.moim.backend.domain.space.repository.SpaceRepository;
import com.moim.backend.domain.space.request.controller.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.request.controller.SpaceTimeLineRequest;
import com.moim.backend.domain.space.response.space.CreateSpaceCalendarResponse;
import com.moim.backend.domain.space.response.space.SpaceTimeLineResponse;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.common.exception.CustomException;
import com.moim.backend.global.util.DateParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moim.backend.domain.space.response.space.SpaceTimeLineResponse.*;
import static com.moim.backend.global.common.Result.NOT_FOUND_GROUP;

@Service
@RequiredArgsConstructor
public class SpaceCalendarService {
    private final SpaceCalendarRepository spaceCalendarRepository;
    private final SpaceRepository spaceRepository;

    public CreateSpaceCalendarResponse createSpaceCalendar(CreateSpaceCalendarRequest request) {
        Space space = getSpace(request.getSpaceId());

        SpaceCalendar spaceCalendar = spaceCalendarRepository.save(SpaceCalendar.builder()
                .space(space)
                .scheduleName(request.getScheduleName())
                .date(request.getDate())
                .dayOfWeek(DateParser.getDayOfWeek(request.getDate()))
                .note(request.getNote())
                .locationName(request.getLocationName())
                .build());

        return CreateSpaceCalendarResponse.response(spaceCalendar);
    }

    public SpaceTimeLineResponse readSpaceTimeLine(SpaceTimeLineRequest request) {
        Map<String, List<Schedule>> timeLine = getDefaultTimeLine(request);
        List<SpaceCalendar> dayOfMonthSchedules = getSpaceDayOfMonthSchedules(request);
        addTimeLine(timeLine, dayOfMonthSchedules);

        return SpaceTimeLineResponse.response(timeLine);
    }

    private List<SpaceCalendar> getSpaceDayOfMonthSchedules(SpaceTimeLineRequest request) {
        LocalDateTime startDate = request.getDate();
        LocalDateTime endDate = getEndDate(startDate);
        Space space = getSpace(request.getSpaceId());
        return spaceCalendarRepository.findBySpaceAndDateBetween(space, startDate, endDate);
    }

    private static void addTimeLine(Map<String, List<Schedule>> timeLine, List<SpaceCalendar> dayOfMonthSchedules) {
        for (SpaceCalendar daySchedule : dayOfMonthSchedules) {
            int dayOfMonth = daySchedule.getDate().getDayOfMonth();
            String dayOfWeek = daySchedule.getDayOfWeek();
            String date = dayOfMonth + "/" + dayOfWeek;

            List<Schedule> timeLineSchedules = timeLine.get(date);
            timeLineSchedules.add(createTimeLineSchedule(daySchedule));
            timeLine.put(date, timeLineSchedules);
        }
    }

    private static Schedule createTimeLineSchedule(SpaceCalendar daySchedule) {
        return Schedule.to(
                daySchedule.getScheduleName(), DateParser.hourAndMinuteFormat(daySchedule.getDate())
        );
    }

    private static Map<String, List<Schedule>> getDefaultTimeLine(SpaceTimeLineRequest request) {
        Map<String, List<Schedule>> timeLine = new HashMap<>();

        int endDayOfMonth = getEndDate(request.getDate()).getDayOfMonth();
        for (int nextDay = 0; nextDay < endDayOfMonth; nextDay++) {
            LocalDateTime day = request.getDate().plusDays(nextDay);
            String dayOfWeek = DateParser.getDayOfWeek(day);
            String date = day.getDayOfMonth() + "/" + dayOfWeek;
            timeLine.put(date, new ArrayList<>());
        }

        return timeLine;
    }

    private Space getSpace(Long spaceId) {
        return spaceRepository.findById(spaceId).orElseThrow(
                () -> new CustomException(NOT_FOUND_GROUP)
        );
    }

    private static LocalDateTime getEndDate(LocalDateTime startDate) {
        YearMonth yearMonth = YearMonth.from(startDate);
        return yearMonth.atEndOfMonth().atTime(23, 59, 59);
    }
}
