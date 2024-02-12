package com.moim.backend.global.util;

import java.time.LocalDateTime;
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
}
