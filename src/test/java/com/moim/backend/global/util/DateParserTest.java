package com.moim.backend.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateParserTest {
    @DisplayName("LocalDateTime 객체를 String 으로 파싱한다.")
    @Test
    void timeFormat() {
        // given
        LocalDateTime time = LocalDateTime.of(2024, 2, 9, 15, 30, 30);

        // when
        String str = DateParser.timeFormat(time);

        // then
        assertThat(str).isEqualTo("2024.02.09(금) 15:30");
    }
}