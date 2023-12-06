package com.moim.backend.docs.groupvote;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.groupvote.controller.VoteController;
import com.moim.backend.domain.groupvote.request.controller.VoteCreateRequest;
import com.moim.backend.domain.groupvote.request.service.VoteCreateServiceRequest;
import com.moim.backend.domain.groupvote.response.VoteCreateResponse;
import com.moim.backend.domain.groupvote.response.VoteSelectPlaceUserResponse;
import com.moim.backend.domain.groupvote.response.VoteSelectResultResponse;
import com.moim.backend.domain.groupvote.service.VoteService;
import com.moim.backend.domain.user.entity.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
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
        VoteCreateRequest request = VoteCreateRequest.toRequest(true, true, null);
        given(voteService.createVote(any(VoteCreateServiceRequest.class), anyLong(), any(Users.class)))
                .willReturn(
                        VoteCreateResponse.builder()
                                .voteId(1L)
                                .groupId(1L)
                                .isClosed(false)
                                .isAnonymous(true)
                                .isEnabledMultipleChoice(false)
                                .endAt("2023-08-13 15:00:00")
                                .build()
                );

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.post("/group/{groupId}/vote", 1L)
                .header(AUTHORIZATION, "Bearer {token}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("투표 API")
                .summary("투표 시작 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .pathParameters(
                        parameterWithName("groupId")
                                .description("그룹 ID"))
                .requestFields(
                        fieldWithPath("isAnonymous").type(JsonFieldType.BOOLEAN)
                                .description("익명 여부"),
                        fieldWithPath("isEnabledMultipleChoice").type(JsonFieldType.BOOLEAN)
                                .description("중복 선택 여부"),
                        fieldWithPath("endAt").type(JsonFieldType.STRING)
                                .description("종료 날짜 / 형식 : yyyy-MM-ddTHH:mm:ss")
                                .optional())
                .responseFields(
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
                                .description("종료 날짜 / 없으면 'none' 반환 "))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("create-vote", prettyPrint(), prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("투표 참여 API")
    @Test
    void selectVote() throws Exception {
        // given
        VoteSelectResultResponse mockResult = VoteSelectResultResponse.builder()
                .groupId(1L)
                .groupName("모이닷 모임")
                .groupDate("2023-08-04")
                .voteId(3L)
                .isClosed(false)
                .isAnonymous(true)
                .isEnabledMultipleChoice(true)
                .endAt("2023-08-10T18:00:00")
                .isVotingParticipant(true)
                .voteStatuses(createMockVoteStatuses())
                .build();

        List<String> bestPlaceIds2 = List.of("4", "6");
        String[] bestPlaceIds = bestPlaceIds2.toArray(new String[0]);

        given(voteService.selectVote(anyLong(), any(), any(), any())).willReturn(mockResult);

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.post("/group/{groupId}/vote/select", 1L)
                .header(AUTHORIZATION, "Bearer {token}")
                .param("bestPlaceIds", bestPlaceIds);

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("투표 API")
                .summary("투표 참여 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .pathParameters(
                        parameterWithName("groupId")
                                .description("그룹 ID"))
                .formParameters(
                        parameterWithName("bestPlaceIds")
                                .description("투표할 장소 ID : List<Long>"))
                .responseFields(
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
                        fieldWithPath("data.isVotingParticipant").type(JsonFieldType.BOOLEAN)
                                .description("내가 투표했는지에 대한 여부"),
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
                                .description("사용자의 투표 여부"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("select-vote", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("투표 현황 조회 API")
    @Test
    void readVote() throws Exception {
        VoteSelectResultResponse mockResult = VoteSelectResultResponse.builder()
                .groupId(1L)
                .groupName("모이닷 모임")
                .groupDate("2023-08-04")
                .voteId(3L)
                .isClosed(false)
                .isAnonymous(true)
                .isEnabledMultipleChoice(true)
                .endAt("2023-08-10 18:00:00")
                .isVotingParticipant(true)
                .voteStatuses(createMockVoteStatuses())
                .build();

        given(voteService.readVote(anyLong(), any()))
                .willReturn(mockResult);

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/group/{groupId}/vote", 1L)
                .header(AUTHORIZATION, "Bearer {token}");

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("투표 API")
                .summary("투표 현황 조회 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .pathParameters(
                        parameterWithName("groupId").description("그룹 ID"))
                .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("상태 메세지"),
                        fieldWithPath("data.groupId").type(JsonFieldType.NUMBER).description("그룹 ID"),
                        fieldWithPath("data.groupName").type(JsonFieldType.STRING).description("그룹 이름"),
                        fieldWithPath("data.groupDate").type(JsonFieldType.STRING).description("그룹 날짜"),
                        fieldWithPath("data.voteId").type(JsonFieldType.NUMBER).description("투표 ID"),
                        fieldWithPath("data.isClosed").type(JsonFieldType.BOOLEAN).description("투표 종료 여부"),
                        fieldWithPath("data.isAnonymous").type(JsonFieldType.BOOLEAN).description("익명 투표 여부"),
                        fieldWithPath("data.isEnabledMultipleChoice").type(JsonFieldType.BOOLEAN).description("다중 선택 가능 여부"),
                        fieldWithPath("data.endAt").type(JsonFieldType.STRING).description("투표 종료 일시"),
                        fieldWithPath("data.isVotingParticipant").type(JsonFieldType.BOOLEAN).description("내가 투표했는지에 대한 여부"),
                        fieldWithPath("data.voteStatuses[].bestPlaceId").type(JsonFieldType.NUMBER).description("장소 ID"),
                        fieldWithPath("data.voteStatuses[].votes").type(JsonFieldType.NUMBER).description("장소 투표 수"),
                        fieldWithPath("data.voteStatuses[].placeName").type(JsonFieldType.STRING).description("장소 이름"),
                        fieldWithPath("data.voteStatuses[].latitude").type(JsonFieldType.NUMBER).description("장소 위도"),
                        fieldWithPath("data.voteStatuses[].longitude").type(JsonFieldType.NUMBER).description("장소 경도"),
                        fieldWithPath("data.voteStatuses[].isVoted").type(JsonFieldType.BOOLEAN).description("사용자의 투표 여부"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("read-vote", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("해당 장소 투표한 인원 리스트 조회 API")
    @Test
    void readSelectPlaceUsers() throws Exception {
        // given
        given(voteService.readSelectPlaceUsers(anyLong(), any(), any()))
                .willReturn(List.of(
                                VoteSelectPlaceUserResponse.builder()
                                        .participationId(1L)
                                        .userId(1L)
                                        .nickName("모이닷 모임장")
                                        .isAdmin(true)
                                        .build(),
                                VoteSelectPlaceUserResponse.builder()
                                        .participationId(2L)
                                        .userId(2L)
                                        .nickName("모이닷 인원1")
                                        .isAdmin(false)
                                        .build(),
                                VoteSelectPlaceUserResponse.builder()
                                        .participationId(3L)
                                        .userId(3L)
                                        .nickName("모이닷 인원2")
                                        .isAdmin(false)
                                        .build()
                        )
                );

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/group/{groupId}/vote/select", 1L)
                .header(AUTHORIZATION, "Bearer {token}")
                .param("bestPlaceId", "1");

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("투표 API")
                .summary("장소에 투표한 인원 조회 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .pathParameters(
                        parameterWithName("groupId")
                                .description("그룹 ID"))
                .queryParameters(
                        parameterWithName("bestPlaceId")
                                .description("추천된 장소 Id"))
                .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                .description("상태 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("상태 메세지"),
                        fieldWithPath("data[].participationId").type(JsonFieldType.NUMBER)
                                .description("참여 ID"),
                        fieldWithPath("data[].userId").type(JsonFieldType.NUMBER)
                                .description("유저 ID"),
                        fieldWithPath("data[].nickName").type(JsonFieldType.STRING)
                                .description("그룹 내 닉네임"),
                        fieldWithPath("data[].isAdmin").type(JsonFieldType.BOOLEAN)
                                .description("관리자 여부"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("read-selectPlace", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("투표 종료하기 API")
    @Test
    void conclusionVote() throws Exception {
        // given
        VoteSelectResultResponse mockResult = VoteSelectResultResponse.builder()
                .groupId(1L)
                .groupName("모이닷 모임")
                .groupDate("2023-08-04")
                .confirmPlace("성신여대입구역")
                .voteId(3L)
                .isClosed(true)
                .isAnonymous(true)
                .isEnabledMultipleChoice(true)
                .endAt("2023-08-10 18:00:00")
                .isVotingParticipant(true)
                .voteStatuses(createMockVoteStatuses())
                .build();


        given(voteService.conclusionVote(anyLong(), any()))
                .willReturn(mockResult);

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.patch("/group/{groupId}/vote", 1L)
                .header(AUTHORIZATION, "Bearer {token}");

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("투표 API")
                .summary("투표 종료하기 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .pathParameters(
                        parameterWithName("groupId")
                                .description("그룹 ID"))
                .responseFields(
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
                        fieldWithPath("data.confirmPlace").type(JsonFieldType.STRING).
                                description("확정된 모임 장소"),
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
                        fieldWithPath("data.isVotingParticipant").type(JsonFieldType.BOOLEAN)
                                .description("내가 투표했는지에 대한 여부"),
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
                                .description("사용자의 투표 여부"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("conclusion-vote", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    // method
    private List<VoteSelectResultResponse.VoteStatus> createMockVoteStatuses() {
        List<VoteSelectResultResponse.VoteStatus> voteStatuses = new ArrayList<>();
        voteStatuses.add(VoteSelectResultResponse.VoteStatus.builder()
                .bestPlaceId(4L)
                .votes(4)
                .placeName("강남역")
                .latitude(37.498085)
                .longitude(127.027621)
                .isVoted(true)
                .build());
        voteStatuses.add(VoteSelectResultResponse.VoteStatus.builder()
                .bestPlaceId(5L)
                .votes(1)
                .placeName("역삼역")
                .latitude(37.500622)
                .longitude(127.036585)
                .isVoted(false)
                .build());
        voteStatuses.add(VoteSelectResultResponse.VoteStatus.builder()
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
