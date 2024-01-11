package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.ControllerTestSupport;
import com.moim.backend.domain.space.request.controller.GroupCreateRequest;
import com.moim.backend.domain.space.request.controller.GroupNameUpdateRequest;
import com.moim.backend.domain.space.request.controller.GroupParticipateRequest;
import com.moim.backend.domain.space.request.controller.GroupParticipateUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static com.moim.backend.domain.space.entity.TransportationType.PUBLIC;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupControllerTest extends ControllerTestSupport {

    @DisplayName("모임 생성 API")
    @Test
    void createGroup() throws Exception {
        // given
        GroupCreateRequest request =
                GroupCreateRequest.toRequest(
                        "테스트 그룹", LocalDate.of(2023, 7, 15), "천이닷",
                        "서울 성북구 보문로34다길 2", 37.591043, 127.019721,
                        PUBLIC, null
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/group")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 생성 API - 그룹 이름 미입력 실패")
    @Test
    void createGroupBlankGroupNameException() throws Exception {
        // given
        GroupCreateRequest request =
                GroupCreateRequest.toRequest(
                        " ", LocalDate.of(2023, 7, 15), "천이닷",
                        "서울 성북구 보문로34다길 2", 37.591043, 127.019721,
                        PUBLIC, null
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/group")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("모임 참여 API")
    @Test
    void participationGroup() throws Exception {
        // given
        GroupParticipateRequest request = GroupParticipateRequest.toRequest(
                1L, "꿀보이스", "쇼파르", 37.5660, 126.9784, PUBLIC, "123456"
        );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/group/participate")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 참여 API - 위도 미입력 실패")
    @Test
    void participationGroupFailsWhenLatitudeNotProvided() throws Exception {
        // given
        GroupParticipateRequest request = GroupParticipateRequest.toRequest(
                1L, "꿀보이스", "쇼파르", null, 126.9784, PUBLIC, "abc123"
        );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/group/participate")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("내 참여 정보 수정 API")
    @Test
    void participationUpdate() throws Exception {
        // given
        GroupParticipateUpdateRequest request = GroupParticipateUpdateRequest.toRequest(
                1L, "양파쿵야", "쇼파르",
                37.5660, 126.9784, PUBLIC
        );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/group/participate")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("내 참여 정보 수정 API - 닉네임 미입력 실패")
    @Test
    void participationUpdateFailsWhenUserNameNotProvided() throws Exception {
        // given
        GroupParticipateUpdateRequest request = GroupParticipateUpdateRequest.toRequest(
                1L, " ", "쇼파르",
                37.5660, 126.9784, PUBLIC
        );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/group/participate")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("모임 나가기 API")
    @Test
    void participationExit() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/group/participate")
                                .header("Authorization", "JWT AccessToken")
                                .param("participateId", String.valueOf(123L))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 전체 나가기 API")
    @Test
    void allParticipateExit() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/group/participate/all")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 나가기 API - 참여자 ID 미입력 실패")
    @Test
    void failWhenParticipantIdNotProvided() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/group/participate")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("모임원 내보내기 API")
    @Test
    void participateRemoval() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/group/participate/removal")
                                .header("Authorization", "JWT AccessToken")
                                .param("participateId", String.valueOf(1L))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 삭제하기 API")
    @Test
    void groupDelete() throws Exception {
        // given
        // when// then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/group")
                                .header("Authorization", "JWT AccessToken")
                                .param("groupId", String.valueOf(1L))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("내 모임 확인하기 API")
    @Test
    void getMyParticipate() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/group/participate")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 장소 추천 조회 리스트 API")
    @Test
    void keywordCentralizedMeetingSpot() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/group/best-region/place")
                                .param("x", "127.232943")
                                .param("y", "37.6823811")
                                .param("local", "성신여대입구역")
                                .param("keyword", "식당")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 참여자 정보 리스트 조회 API")
    @Test
    void readParticipateGroupByRegion() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/group")
                                .param("groupId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("모임 이름 수정 API")
    @Test
    void updateGroupName() throws Exception {
        // given
        GroupNameUpdateRequest request = new GroupNameUpdateRequest("모이닷런칭준비");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/group")
                                .header("Authorization", "JWT AccessToken")
                                .param("groupId", "1")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}