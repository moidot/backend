package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.ControllerTestSupport;
import com.moim.backend.domain.space.request.GroupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupControllerTest extends ControllerTestSupport {

    @DisplayName("모임 생성 API")
    @Test
    void createGroup() throws Exception {
        // given
        GroupRequest.Create request =
                new GroupRequest.Create("테스트 그룹", LocalDateTime.of(2023, 7, 13, 13, 0));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/group")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 생성 API - 그룹 이름 미입력 실패")
    @Test
    void createGroupBlankGroupNameException() throws Exception {
        // given
        GroupRequest.Create request =
                new GroupRequest.Create(" ", LocalDateTime.of(2023, 7, 13, 13, 0));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/group")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("모임 참여 API")
    @Test
    void participationGroup() throws Exception {
        // given
        GroupRequest.Participate request
                = new GroupRequest.Participate(1L, 37.5660, 126.9784, "BUS", "abc123");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/group/participate")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 참여 API - 위도 미입력 실패")
    @Test
    void participationGroupFailsWhenLatitudeNotProvided() throws Exception {
        // given
        GroupRequest.Participate request
                = new GroupRequest.Participate(1L, null, 126.9784, "BUS", "abc123");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/group/participate")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}