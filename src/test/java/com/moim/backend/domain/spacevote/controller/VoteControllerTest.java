package com.moim.backend.domain.spacevote.controller;

import com.moim.backend.domain.ControllerTestSupport;
import com.moim.backend.domain.spacevote.request.controller.VoteCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTest extends ControllerTestSupport {

    @DisplayName("투표 생성 API")
    @Test
    void createVote() throws Exception {
        // given
        VoteCreateRequest request = VoteCreateRequest.toRequest(true, true, null);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/group/{groupId}/vote", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("투표 생성 API - 익명 여부 미등록 실패")
    @Test
    void createVoteWithNotInsertAnonymousThrowException() throws Exception {
        // given
        VoteCreateRequest request = VoteCreateRequest.toRequest(null, true, null);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/group/{groupId}/vote", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
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
                        MockMvcRequestBuilders.post("/group/{groupId}/vote/select", 1L)
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
                        MockMvcRequestBuilders.get("/group/{groupId}/vote", 1L)
                                .param("user", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("해당 장소 투표한 인원 리스트 조회 API")
    @Test
    void readSelectPlaceUsers() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/group/{groupId}/vote/select", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .param("bestPlaceId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("투표 종료하기 API")
    @Test
    void conclusionVote() throws Exception {
        // given

        // when// then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/group/{groupId}/vote", 1L)
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("재투표 API")
    @Test
    void reCreateVote() throws Exception {
        // given
        VoteCreateRequest request = VoteCreateRequest.toRequest(true, true, null);

        // when// then
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/group/{spaceId}/vote", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}