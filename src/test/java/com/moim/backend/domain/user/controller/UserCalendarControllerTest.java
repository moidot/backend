package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.ControllerTestSupport;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
import com.moim.backend.domain.user.request.UserCalendarPageRequest;
import com.moim.backend.domain.user.request.UserDetailCalendarRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserCalendarControllerTest extends ControllerTestSupport {
    @DisplayName("개인 캘린더 일정 추가 API")
    @Test
    void createCalendar() throws Exception {
        // given
        CreateUserCalendarRequest request =
                new CreateUserCalendarRequest("모이닷 모임", LocalDateTime.of(2024, 1, 24, 15, 38, 15), "메모", "종로 2가", "R");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/calendar")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("캘린더 조회 API")
    @Test
    void readCalendar() throws Exception {
        // given
        UserCalendarPageRequest request = new UserCalendarPageRequest(LocalDateTime.of(2024, 2, 1, 0, 0, 0));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/calendar")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("해당 날짜 일정 조회 API")
    @Test
    void readDetailCalendar() throws Exception {
        // given
        UserDetailCalendarRequest request = new UserDetailCalendarRequest(LocalDateTime.of(2024, 2, 9, 0, 0, 0));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/calendar/detail")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}