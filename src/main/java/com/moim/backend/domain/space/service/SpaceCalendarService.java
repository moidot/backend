package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.SpaceCalendar;
import com.moim.backend.domain.space.repository.SpaceCalendarRepository;
import com.moim.backend.domain.space.repository.SpaceRepository;
import com.moim.backend.domain.space.request.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.request.SpaceTimeLineRequest;
import com.moim.backend.domain.space.response.space.CreateSpaceCalendarResponse;
import com.moim.backend.domain.space.response.space.SpaceTimeLineResponse;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.moim.backend.domain.space.response.space.SpaceTimeLineResponse.Schedule;
import static com.moim.backend.global.common.Result.NOT_FOUND_GROUP;
import static com.moim.backend.global.util.DateParser.*;

@Service
@RequiredArgsConstructor
public class SpaceCalendarService {
    private final SpaceCalendarRepository spaceCalendarRepository;
    private final SpaceRepository spaceRepository;

    // 모임 캘린더 일정 추가 API
    public CreateSpaceCalendarResponse createSpaceCalendar(CreateSpaceCalendarRequest request) {
        Space space = getSpace(request.getSpaceId());

        SpaceCalendar spaceCalendar = spaceCalendarRepository.save(SpaceCalendar.builder()
                .space(space)
                .scheduleName(request.getScheduleName())
                .date(request.getDate())
                .dayOfWeek(getDayOfWeek(request.getDate()))
                .note(request.getNote())
                .locationName(request.getLocationName())
                .build());

        return CreateSpaceCalendarResponse.response(spaceCalendar);
    }

    // 타임라인 조회 API
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
                daySchedule.getScheduleName(), hourAndMinuteFormat(daySchedule.getDate())
        );
    }

    private static Map<String, List<Schedule>> getDefaultTimeLine(SpaceTimeLineRequest request) {
        Map<String, List<Schedule>> timeLine = new LinkedHashMap<>();

        int endDayOfMonth = getEndDate(request.getDate()).getDayOfMonth();
        for (int nextDay = 0; nextDay < endDayOfMonth; nextDay++) {
            LocalDateTime day = request.getDate().plusDays(nextDay);
            String dayOfWeek = getDayOfWeek(day);
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
}
