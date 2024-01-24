package com.moim.backend.domain.user.response;

import com.moim.backend.domain.user.entity.UserCalendar;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
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
public class CreateUserCalendarResponse {
    private Long userCalendarId;
    private Long userId;
    private String scheduleName;
    private String date;
    private String dayOfWeek;
    private String note;
    private String locationName;
    private String color;

    public static CreateUserCalendarResponse response(UserCalendar userCalendar) {
        return CreateUserCalendarResponse.builder()
                .userCalendarId(userCalendar.getUserCalendarId())
                .userId(userCalendar.getUser().getUserId())
                .scheduleName(userCalendar.getScheduleName())
                .date(formatDate(userCalendar.getDate()))
                .dayOfWeek(userCalendar.getDayOfWeek())
                .note(userCalendar.getNote())
                .locationName(userCalendar.getLocationName())
                .color(userCalendar.getColor())
                .build();
    }

    private static String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return date.format(formatter);
    }
}
