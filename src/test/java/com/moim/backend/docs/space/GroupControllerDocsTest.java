package com.moim.backend.docs.space;

import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.space.controller.GroupController;
import com.moim.backend.domain.space.request.GroupRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.space.service.GroupService;
import com.moim.backend.domain.subway.response.BestSubway;
import com.moim.backend.domain.subway.response.BestSubwayInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.misusing.CannotStubVoidMethodWithReturnValue;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GroupControllerDocsTest extends RestDocsSupport {

    private final GroupService groupService = mock(GroupService.class);

    @Override
    protected Object initController() {
        return new GroupController(groupService);
    }

    @DisplayName("모임 생성 API")
    @Test
    void createGroup() throws Exception {
        // given
        GroupRequest.Create request =
                new GroupRequest.Create("테스트 그룹", null);

        given(groupService.createGroup(any(), any()))
                .willReturn(
                        GroupResponse.Create.builder()
                                .groupId(1L)
                                .adminId(1L)
                                .name("모이닷 모임")
                                .date("2023-07-13")
                                .fixedPlace("none")
                                .build()
                );

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/group")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("group-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("모임 이름"),
                                fieldWithPath("date").type(JsonFieldType.STRING)
                                        .description("모임 날짜 / 'yyyy-dd-mm'")
                                        .optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.groupId").type(JsonFieldType.NUMBER)
                                        .description("모임 ID / Long"),
                                fieldWithPath("data.adminId").type(JsonFieldType.NUMBER)
                                        .description("모임장 ID / Long"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("모임 이름"),
                                fieldWithPath("data.date").type(JsonFieldType.STRING)
                                        .description("모임 날짜"),
                                fieldWithPath("data.fixedPlace").type(JsonFieldType.STRING)
                                        .description("확정 장소")
                        )
                ));
    }

    @DisplayName("모임 참여 API")
    @Test
    void participationGroup() throws Exception {
        // given
        GroupRequest.Participate request
                = new GroupRequest.Participate(1L, "안지영", "쇼파르", 37.5660, 126.9784, "BUS", "123456");

        given(groupService.participateGroup(any(), any()))
                .willReturn(
                        GroupResponse.Participate.builder()
                                .participationId(1L)
                                .groupId(1L)
                                .userId(1L)
                                .userName("안지영")
                                .locationName("쇼파르")
                                .latitude(37.57449)
                                .longitude(126.89521)
                                .transportation("BUS")
                                .build()
                );

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/group/participate")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("group-participation",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("groupId").type(JsonFieldType.NUMBER)
                                        .description("그룹 ID / Long"),
                                fieldWithPath("userName").type(JsonFieldType.STRING)
                                        .description("유저 별명"),
                                fieldWithPath("locationName").type(JsonFieldType.STRING)
                                        .description("출발 위치 이름"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("위도 / Double"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("경도 / Double"),
                                fieldWithPath("transportation").type(JsonFieldType.STRING)
                                        .description("'BUS' / 'SUBWAY'"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("모임 내 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.participationId").type(JsonFieldType.NUMBER)
                                        .description("모임 참여자 ID / Long"),
                                fieldWithPath("data.groupId").type(JsonFieldType.NUMBER)
                                        .description("그룹 ID / Long"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .description("유저 ID / Long"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("유저 별명"),
                                fieldWithPath("data.locationName").type(JsonFieldType.STRING)
                                        .description("출발 위치"),
                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER)
                                        .description("위도 / Double"),
                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER)
                                        .description("경도 / Long"),
                                fieldWithPath("data.transportation").type(JsonFieldType.STRING)
                                        .description("내 이동수단")
                        )
                ));
    }

    @DisplayName("내 참여 정보 수정 API")
    @Test
    void participationUpdate() throws Exception {
        // given
        GroupRequest.ParticipateUpdate request
                = new GroupRequest.ParticipateUpdate(1L, "양파쿵야", "쇼파르", 37.5660, 126.9784, "SUBWAY");

        given(groupService.participateUpdate(any(), any()))
                .willReturn(
                        GroupResponse.ParticipateUpdate.builder()
                                .locationName("쇼파르")
                                .transportation("SUBWAY")
                                .build()
                );

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/api/v1/group/participate")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("participate-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("participateId").type(JsonFieldType.NUMBER)
                                        .description("참여 정보 ID / Long"),
                                fieldWithPath("userName").type(JsonFieldType.STRING)
                                        .description("유저 별명"),
                                fieldWithPath("locationName").type(JsonFieldType.STRING)
                                        .description("출발 위치 이름"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("위도 / Double"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("경도 / Double"),
                                fieldWithPath("transportation").type(JsonFieldType.STRING)
                                        .description("'BUS' / 'SUBWAY'")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.locationName").type(JsonFieldType.STRING)
                                        .description("출발 위치"),
                                fieldWithPath("data.transportation").type(JsonFieldType.STRING)
                                        .description("내 이동수단")
                        )
                ));
    }

    @DisplayName("모임 나가기 API")
    @Test
    void participationExit() throws Exception {
        // given
        given(groupService.participateExit(any(), any()))
                .willReturn(
                        GroupResponse.Exit.builder()
                                .isDeletedSpace(false)
                                .message("모임에서 나갔습니다.")
                                .build()
                );
        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/v1/group/participate")
                                .header("Authorization", "JWT AccessToken")
                                .param("participateId", String.valueOf(1L))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("participation-exit",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        formParameters(
                                parameterWithName("participateId")
                                        .description("참여자 정보 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.isDeletedSpace").type(JsonFieldType.BOOLEAN)
                                        .description("모임 삭제 여부 : 어드민이 나간경우 모임이 삭제 / 참가자가 나간경우 모임 나가기"),
                                fieldWithPath("data.message").type(JsonFieldType.STRING)
                                        .description("모임이 삭제되었습니다. / 모임에서 나갔습니다.")
                        )
                ));
    }

    @DisplayName("모임원 내보내기 API")
    @Test
    void participateRemoval() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/v1/group/participate/removal")
                                .header("Authorization", "JWT AccessToken")
                                .param("participateId", String.valueOf(1L))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("participate-removal",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        formParameters(
                                parameterWithName("participateId")
                                        .description("참여자 정보 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("Always NULL")
                        )
                ));
    }

    @DisplayName("모임 삭제하기 API")
    @Test
    void groupDelete() throws Exception {
        // given
        // when// then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/v1/group")
                                .header("Authorization", "JWT AccessToken")
                                .param("groupId", String.valueOf(1L))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("group-delete",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        formParameters(
                                parameterWithName("groupId")
                                        .description("그룹 ID")
                        )
                ));
    }

    @DisplayName("모임 추천 지역 조회하기 API")
    @Test
    void getBestRegion() throws Exception {
        // given
        List<BestSubwayInterface> bestSubwayList = List.of(
                new BestSubway("독바위", 37.618456, 126.933031, 2842.204594299132),
                new BestSubway("불광", 37.610553, 126.92982, 3392.8990231398966),
                new BestSubway("불광", 37.610873, 126.92939, 3412.2536548883427),
                new BestSubway("녹번", 37.600927, 126.935756, 3582.0186756314224),
                new BestSubway("연신내", 37.619229, 126.921038, 3870.912704056272),
                new BestSubway("연신내", 37.618636, 126.920625, 3915.770625983176),
                new BestSubway("북한산보국문", 37.612072, 127.008251, 4049.387636179618),
                new BestSubway("역촌", 37.606021, 126.922744, 4176.163056775289),
                new BestSubway("홍제", 37.589066, 126.943736, 4258.981576036244),
                new BestSubway("구파발", 37.636763, 126.918821, 4292.193359562507)
        );
        given(groupService.getBestRegion(any()))
                .willReturn(bestSubwayList);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/group/best-region")
                                .param("groupId", String.valueOf(14L))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-best-region",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("groupId")
                                        .description("모이닷 스페이스 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .description("지하철역 이름"),
                                fieldWithPath("data[].latitude").type(JsonFieldType.NUMBER)
                                        .description("지하철역 위도"),
                                fieldWithPath("data[].longitude").type(JsonFieldType.NUMBER)
                                        .description("지하철역 경도"),
                                fieldWithPath("data[].distanceFromMiddlePoint").type(JsonFieldType.NUMBER)
                                        .description("중간좌표로부터 지하철역까지의 거리(단위: m)")
                        )
                ));
    }

    @DisplayName("내 모임 확인하기 API")
    @Test
    void getMyParticipate() throws Exception {
        // given
        GroupResponse.MyParticipate data1 = GroupResponse.MyParticipate.builder()
                .groupId(1L)
                .groupName("그룹1")
                .groupDate("2023-07-15")
                .groupParticipates(3)
                .bestPlaces(
                        List.of(
                                GroupResponse.BestPlaces.builder()
                                        .bestPlaceId(764L)
                                        .bestPlaceName("종로5가역")
                                        .build(),
                                GroupResponse.BestPlaces.builder()
                                        .bestPlaceId(765L)
                                        .bestPlaceName("종로3가역")
                                        .build(),
                                GroupResponse.BestPlaces.builder()
                                        .bestPlaceId(763L)
                                        .bestPlaceName("동대문역")
                                        .build()
                        )
                )
                .build();

        GroupResponse.MyParticipate data2 = GroupResponse.MyParticipate.builder()
                .groupId(2L)
                .groupName("그룹2")
                .groupDate("2023-07-28")
                .groupParticipates(7)
                .bestPlaces(
                        List.of(
                                GroupResponse.BestPlaces.builder()
                                        .bestPlaceId(737L)
                                        .bestPlaceName("강남역")
                                        .build(),
                                GroupResponse.BestPlaces.builder()
                                        .bestPlaceId(736L)
                                        .bestPlaceName("교대역")
                                        .build(),
                                GroupResponse.BestPlaces.builder()
                                        .bestPlaceId(738L)
                                        .bestPlaceName("역삼역")
                                        .build()
                        )
                )
                .build();

        given(groupService.getMyParticipate(any()))
                .willReturn(List.of(data1, data2));

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/group/participate")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("my-participate",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("insert the AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data[].groupId").type(JsonFieldType.NUMBER)
                                        .description("그룹 ID"),
                                fieldWithPath("data[].groupName").type(JsonFieldType.STRING)
                                        .description("그룹 이름"),
                                fieldWithPath("data[].groupDate").type(JsonFieldType.STRING)
                                        .description("그룹 모임날짜"),
                                fieldWithPath("data[].groupParticipates").type(JsonFieldType.NUMBER)
                                        .description("그룹 참여자 수 / Integer"),
                                fieldWithPath("data[].bestPlaces[]").type(JsonFieldType.ARRAY)
                                        .description("그룹 추천장소 현황"),
                                fieldWithPath("data[].bestPlaces[].bestPlaceId").type(JsonFieldType.NUMBER)
                                        .description("그룹 추천장소 ID / Long"),
                                fieldWithPath("data[].bestPlaces[].bestPlaceName").type(JsonFieldType.STRING)
                                        .description("그룹 추천장소 이름")
                                )
                ));
    }
}
