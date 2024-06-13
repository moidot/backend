package com.moim.backend.domain.space.response.space;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SpaceTimeLineResponse {
    private Map<String, List<Schedule>> timeLine;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Schedule {
        private String scheduleName;
        private String date;

        public static Schedule to(String scheduleName, String date) {
            return Schedule.builder()
                    .scheduleName(scheduleName)
                    .date(date)
                    .build();
        }
    }

    public static SpaceTimeLineResponse response(Map<String, List<Schedule>> timeLine) {
        return SpaceTimeLineResponse.builder()
                .timeLine(timeLine)
                .build();
    }
}
