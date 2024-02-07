package com.moim.backend.domain.space.response.space;

import com.moim.backend.domain.space.entity.SpaceCalendar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class CreateSpaceCalendarResponse {
    private Long spaceCalendarId;
    private Long spaceId;
    private String scheduleName;
    private String date;
    private String dayOfWeek;
    private String note;
    private String locationName;

    public static CreateSpaceCalendarResponse response(SpaceCalendar spaceCalendar) {
        return CreateSpaceCalendarResponse.builder()
                .spaceCalendarId(spaceCalendar.getSpaceCalendarId())
                .spaceId(spaceCalendar.getSpace().getSpaceId())
                .scheduleName(spaceCalendar.getScheduleName())
                .date(formatDate(spaceCalendar.getDate()))
                .dayOfWeek(spaceCalendar.getDayOfWeek())
                .note(spaceCalendar.getNote())
                .locationName(spaceCalendar.getLocationName())
                .build();
    }

    private static String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return date.format(formatter);
    }
}
