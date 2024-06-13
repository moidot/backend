package com.moim.backend.global.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

import static java.time.format.TextStyle.*;
import static java.util.Locale.KOREAN;

public class DateParser {
    public static String timeFormat(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E) HH:mm", KOREAN);
        return date.format(formatter);
    }

    public static String hourAndMinuteFormat(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a h:mm", KOREAN);
        return date.format(formatter);
    }

    public static String getDayOfWeek(LocalDateTime date) {
        return date.getDayOfWeek().getDisplayName(SHORT, KOREAN);
    }

    public static LocalDateTime getEndDate(LocalDateTime startDate) {
        YearMonth yearMonth = YearMonth.from(startDate);
        return yearMonth.atEndOfMonth().atTime(23, 59, 59);
    }

    public static LocalDateTime getEndDateTime(LocalDateTime startDateTime) {
        return startDateTime.with(LocalTime.MAX);
    }
}
