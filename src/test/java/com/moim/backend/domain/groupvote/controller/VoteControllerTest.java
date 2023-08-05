package com.moim.backend.domain.groupvote.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.moim.backend.domain.ControllerTestSupport;
import com.moim.backend.domain.groupvote.request.VoteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTest extends ControllerTestSupport {

    @DisplayName("투표 생성 API")
    @Test
    void createVote() throws Exception {
        // given
        VoteRequest.Create request = new VoteRequest.Create(true, true, null);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/group/{groupId}/vote", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("투표 생성 API - 익명 여부 미등록 실패")
    @Test
    void createVoteWithNotInsertAnonymousThrowException() throws Exception {
        // given
        VoteRequest.Create request = new VoteRequest.Create(null, true, null);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/group/{groupId}/vote", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @DisplayName("투표 참여 API")
    @Test
    void selectVote() throws Exception {
        // given
        List<Long> bestPlaceIds = List.of(1L, 2L);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/group/{groupId}/vote/select", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .param("bestPlaceIds", StringUtils.collectionToCommaDelimitedString(bestPlaceIds))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("투표 현황 조회 API")
    @Test
    void readVote() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/group/{groupId}/vote", 1L)
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}