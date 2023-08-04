package com.moim.backend.docs.groupvote;

import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.groupvote.controller.VoteController;
import com.moim.backend.domain.groupvote.request.VoteRequest;
import com.moim.backend.domain.groupvote.request.VoteServiceRequest;
import com.moim.backend.domain.groupvote.response.VoteResponse;
import com.moim.backend.domain.groupvote.service.VoteService;
import com.moim.backend.domain.user.entity.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VoteControllerDocsTest extends RestDocsSupport {

    private final VoteService voteService = mock(VoteService.class);

    @Override
    protected Object initController() {
        return new VoteController(voteService);
    }

    @DisplayName("투표 생성 API")
    @Test
    void createVote() throws Exception {
        // given
        VoteRequest.Create request = new VoteRequest.Create(true, true, null);
        given(voteService.createVote(any(VoteServiceRequest.Create.class), anyLong(), any(Users.class)))
                .willReturn(
                        VoteResponse.Create.builder()
                                .voteId(1L)
                                .groupId(1L)
                                .isClosed(false)
                                .isAnonymous(true)
                                .isEnabledMultipleChoice(false)
                                .endAt("2023-08-13 15:00:00")
                                .build()
                );

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/group/{groupId}/vote", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("create-vote",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("groupId")
                                        .description("그룹 ID")
                        ),
                        requestFields(
                                fieldWithPath("isAnonymous").type(JsonFieldType.BOOLEAN)
                                        .description("익명 여부"),
                                fieldWithPath("isEnabledMultipleChoice").type(JsonFieldType.BOOLEAN)
                                        .description("중복 선택 여부"),
                                fieldWithPath("endAt").type(JsonFieldType.STRING)
                                        .description("종료 날짜 / 형식 : yyyy-MM-dd-HH:mm:ss")
                                        .optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.voteId").type(JsonFieldType.NUMBER)
                                        .description("투표 Id / Long"),
                                fieldWithPath("data.groupId").type(JsonFieldType.NUMBER)
                                        .description("그룹 Id / Long"),
                                fieldWithPath("data.isClosed").type(JsonFieldType.BOOLEAN)
                                        .description("투표 종료 여부"),
                                fieldWithPath("data.isAnonymous").type(JsonFieldType.BOOLEAN)
                                        .description("익명 여부"),
                                fieldWithPath("data.isEnabledMultipleChoice").type(JsonFieldType.BOOLEAN)
                                        .description("중복 선택 여부"),
                                fieldWithPath("data.endAt").type(JsonFieldType.STRING)
                                        .description("종료 날짜 / 없으면 'none' 반환 ")

                        )
                ));
    }

    @DisplayName("투표 참여 API")
    @Test
    void selectVote() throws Exception {
        // given
        VoteResponse.SelectResult mockResult = VoteResponse.SelectResult.builder()
                .groupId(1L)
                .groupName("모이닷 모임")
                .groupDate("2023-08-04")
                .voteId(3L)
                .isClosed(false)
                .isAnonymous(true)
                .isEnabledMultipleChoice(true)
                .endAt("2023-08-10 18:00:00")
                .voteStatuses(createMockVoteStatuses())
                .build();

        List<String> bestPlaceIds2 = List.of("4", "6");
        String[] bestPlaceIds = bestPlaceIds2.toArray(new String[0]);

        given(voteService.selectVote(anyLong(),any(),any(),any())).willReturn(mockResult);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/group/{groupId}/vote/select", 1L)
                                .header("Authorization", "JWT AccessToken")
                                .param("bestPlaceIds", bestPlaceIds)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("select-vote",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("groupId")
                                        .description("그룹 ID")
                        ),
                        formParameters(
                                parameterWithName("bestPlaceIds")
                                        .description("투표할 장소 ID : List<Long>")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.groupId").type(JsonFieldType.NUMBER)
                                        .description("그룹 ID"),
                                fieldWithPath("data.groupName").type(JsonFieldType.STRING)
                                        .description("그룹 이름"),
                                fieldWithPath("data.groupDate").type(JsonFieldType.STRING).
                                        description("그룹 날짜"),
                                fieldWithPath("data.voteId").type(JsonFieldType.NUMBER)
                                        .description("투표 ID"),
                                fieldWithPath("data.isClosed").type(JsonFieldType.BOOLEAN)
                                        .description("투표 종료 여부"),
                                fieldWithPath("data.isAnonymous").type(JsonFieldType.BOOLEAN)
                                        .description("익명 투표 여부"),
                                fieldWithPath("data.isEnabledMultipleChoice").type(JsonFieldType.BOOLEAN).
                                        description("다중 선택 가능 여부"),
                                fieldWithPath("data.endAt").type(JsonFieldType.STRING)
                                        .description("투표 종료 일시"),
                                fieldWithPath("data.voteStatuses[].bestPlaceId").type(JsonFieldType.NUMBER)
                                        .description("장소 ID"),
                                fieldWithPath("data.voteStatuses[].votes").type(JsonFieldType.NUMBER)
                                        .description("장소 투표 수"),
                                fieldWithPath("data.voteStatuses[].placeName").type(JsonFieldType.STRING)
                                        .description("장소 이름"),
                                fieldWithPath("data.voteStatuses[].latitude").type(JsonFieldType.NUMBER)
                                        .description("장소 위도"),
                                fieldWithPath("data.voteStatuses[].longitude").type(JsonFieldType.NUMBER)
                                        .description("장소 경도"),
                                fieldWithPath("data.voteStatuses[].isVoted").type(JsonFieldType.BOOLEAN)
                                        .description("사용자의 투표 여부")
                        )
                ));
    }

    // method
    private List<VoteResponse.VoteStatus> createMockVoteStatuses() {
        List<VoteResponse.VoteStatus> voteStatuses = new ArrayList<>();
        voteStatuses.add(VoteResponse.VoteStatus.builder()
                .bestPlaceId(4L)
                .votes(4)
                .placeName("강남역")
                .latitude(37.498085)
                .longitude(127.027621)
                .isVoted(true)
                .build());
        voteStatuses.add(VoteResponse.VoteStatus.builder()
                .bestPlaceId(5L)
                .votes(1)
                .placeName("역삼역")
                .latitude(37.500622)
                .longitude(127.036585)
                .isVoted(false)
                .build());
        voteStatuses.add(VoteResponse.VoteStatus.builder()
                .bestPlaceId(6L)
                .votes(3)
                .placeName("신논현역")
                .latitude(37.504493)
                .longitude(127.025637)
                .isVoted(true)
                .build());
        return voteStatuses;
    }
}
