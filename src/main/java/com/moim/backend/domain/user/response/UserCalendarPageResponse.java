package com.moim.backend.domain.user.response;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCalendarPageResponse {
    private Map<Integer, List<Schedule>> calendar;

    public static UserCalendarPageResponse response(Map<Integer, List<Schedule>> calendar) {
        return UserCalendarPageResponse.builder()
                .calendar(calendar)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Schedule {
        private String color;
        private String scheduleName;

        public static UserCalendarPageResponse.Schedule of(String color, String scheduleName) {
            return Schedule.builder()
                    .color(color)
                    .scheduleName(scheduleName)
                    .build();
        }
    }
}
