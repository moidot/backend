package com.moim.backend.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Locale.KOREAN;

public class DateParser {
    public static String timeFormat(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E) HH:mm", KOREAN);
        return date.format(formatter);
    }
}
