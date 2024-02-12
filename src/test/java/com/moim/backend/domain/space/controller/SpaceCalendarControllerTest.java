package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.ControllerTestSupport;
import com.moim.backend.domain.space.request.controller.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.request.controller.SpaceTimeLineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SpaceCalendarControllerTest extends ControllerTestSupport {
    @DisplayName("모임 캘린더 일정 추가 API")
    @Test
    void createCalendar() throws Exception {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 2, 15, 37, 32);
        CreateSpaceCalendarRequest request =
                new CreateSpaceCalendarRequest(1L, "테스트 입니다", localDateTime, "테스트", "안산시 성포동");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/space/calendar")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("타임라인 조회 API")
    @Test
    void readSpaceTimeLine() throws Exception {
        // given
        SpaceTimeLineRequest request = new SpaceTimeLineRequest(1L, LocalDateTime.of(2024, 2, 1, 0, 0, 0));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/space/timeLine")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}