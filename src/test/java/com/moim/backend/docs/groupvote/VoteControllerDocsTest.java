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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
}
