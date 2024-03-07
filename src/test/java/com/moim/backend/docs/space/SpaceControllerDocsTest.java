package com.moim.backend.docs.space;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.space.controller.SpaceController;
import com.moim.backend.domain.space.request.SpaceCreateRequest;
import com.moim.backend.domain.space.request.SpaceNameUpdateRequest;
import com.moim.backend.domain.space.request.SpaceParticipateRequest;
import com.moim.backend.domain.space.request.SpaceParticipateUpdateRequest;
import com.moim.backend.domain.space.response.*;
import com.moim.backend.domain.space.response.space.*;
import com.moim.backend.domain.space.service.SpaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static com.epages.restdocs.apispec.Schema.schema;
import static com.moim.backend.domain.space.entity.TransportationType.PERSONAL;
import static com.moim.backend.domain.space.entity.TransportationType.PUBLIC;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpaceControllerDocsTest extends RestDocsSupport {

    private final SpaceService spaceService = mock(SpaceService.class);

    @Override
    protected Object initController() {
        return new SpaceController(spaceService);
    }

    @DisplayName("모임 생성 API")
    @Test
    void createGroup() throws Exception {
        // given
        SpaceCreateRequest request =
                SpaceCreateRequest.toRequest(
                        "테스트 그룹", null, "천이닷",
                        "서울 성북구 보문로34다길 2", 37.591043, 127.019721,
                        PUBLIC, null
                );

        given(spaceService.createSpace(any(), any()))
                .willReturn(
                        SpaceCreateResponse.builder()
                                .groupId(1L)
                                .adminId(1L)
                                .name("모이닷 모임")
                                .date("2023-07-13")
                                .fixedPlace("none")
                                .build()
                );

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.post("/group")
                .header(AUTHORIZATION, "Bearer {token}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON);

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("스페이스 생성 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .requestFields(
                        fieldWithPath("name").type(STRING).description("모임 이름"),
                        fieldWithPath("date").type(STRING).description("모임 날짜 / 'yyyy-dd-mm'").optional(),
                        fieldWithPath("userName").type(STRING).description("모임에서 사용할 별명"),
                        fieldWithPath("locationName").type(STRING).description("출발 위치"),
                        fieldWithPath("latitude").type(NUMBER).description("위도 / Double"),
                        fieldWithPath("longitude").type(NUMBER).description("경도 / Double"),
                        fieldWithPath("transportationType").type(STRING).description("대중교통 : 'PUBLIC' / 자동차 : 'PERSONAL'"),
                        fieldWithPath("password").type(STRING).description("비밀번호").optional())
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.groupId").type(NUMBER).description("모임 ID / Long"),
                        fieldWithPath("data.adminId").type(NUMBER).description("모임장 ID / Long"),
                        fieldWithPath("data.name").type(STRING).description("모임 이름"),
                        fieldWithPath("data.date").type(STRING).description("모임 날짜"),
                        fieldWithPath("data.fixedPlace").type(STRING).description("확정 장소"))
                .requestSchema(schema("GroupCreateRequest"))
                .responseSchema(schema("GroupCreateResponse"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("group-create", prettyPrint(), prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("모임 참여 API")
    @Test
    void participationGroup() throws Exception {
        // given
        SpaceParticipateRequest request = SpaceParticipateRequest.toRequest(
                1L, "안지영", "서울 성북구 보문로34다길 2",
                37.209043, 126.329194, PUBLIC,
                "123456"
        );

        given(spaceService.participateSpace(any(), any()))
                .willReturn(
                        SpaceParticipateResponse.builder()
                                .participationId(1L)
                                .groupId(1L)
                                .userId(1L)
                                .userName("안지영")
                                .locationName("서울 성북구 보문로34다길 2")
                                .latitude(37.209043)
                                .longitude(126.329194)
                                .transportation("PUBLIC")
                                .build()
                );

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.post("/group/participate")
                .header(AUTHORIZATION, "Bearer {token}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON);

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("스페이스 참여 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .requestFields(
                        fieldWithPath("groupId").type(NUMBER).description("그룹 ID / Long"),
                        fieldWithPath("userName").type(STRING).description("유저 닉네임"),
                        fieldWithPath("locationName").type(STRING).description("출발 위치 이름"),
                        fieldWithPath("latitude").type(NUMBER).description("위도 / Double"),
                        fieldWithPath("longitude").type(NUMBER).description("경도 / Double"),
                        fieldWithPath("transportationType").type(STRING).description("대중교통 : 'PUBLIC' / 자동차 : 'PERSONAL'"),
                        fieldWithPath("password").type(STRING).description("모임 내 비밀번호").optional())
                .responseFields(
                        fieldWithPath("code").type(NUMBER)
                                .description("상태 코드"),
                        fieldWithPath("message").type(STRING)
                                .description("상태 메세지"),
                        fieldWithPath("data.participationId").type(NUMBER)
                                .description("모임 참여자 ID / Long"),
                        fieldWithPath("data.groupId").type(NUMBER)
                                .description("그룹 ID / Long"),
                        fieldWithPath("data.userId").type(NUMBER)
                                .description("유저 ID / Long"),
                        fieldWithPath("data.userName").type(STRING)
                                .description("유저 닉네임"),
                        fieldWithPath("data.locationName").type(STRING)
                                .description("출발 위치"),
                        fieldWithPath("data.latitude").type(NUMBER)
                                .description("위도 / Double"),
                        fieldWithPath("data.longitude").type(NUMBER)
                                .description("경도 / Long"),
                        fieldWithPath("data.transportation").type(STRING)
                                .description("내 이동수단"))
                .responseSchema(schema("GroupParticipateResponse"))
                .requestSchema(schema("GroupParticipateRequest"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("group-participation", prettyPrint(), prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("내 참여 정보 수정 API")
    @Test
    void participationUpdate() throws Exception {
        // given
        SpaceParticipateUpdateRequest request = SpaceParticipateUpdateRequest.toRequest(
                1L, "양파쿵야", "쇼파르",
                37.5660, 126.9784, PERSONAL
        );

        given(spaceService.participateUpdate(any(), any()))
                .willReturn(
                        SpaceParticipateUpdateResponse.builder()
                                .locationName("쇼파르")
                                .transportation("PERSONAL")
                                .build()
                );

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.patch("/group/participate")
                .header(AUTHORIZATION, "Bearer {token}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON);

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("내 참여 정보 수정 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .requestFields(
                        fieldWithPath("participateId").type(NUMBER)
                                .description("참여 정보 ID / Long"),
                        fieldWithPath("userName").type(STRING)
                                .description("유저 닉네임"),
                        fieldWithPath("locationName").type(STRING)
                                .description("출발 위치 이름"),
                        fieldWithPath("latitude").type(NUMBER)
                                .description("위도 / Double"),
                        fieldWithPath("longitude").type(NUMBER)
                                .description("경도 / Double"),
                        fieldWithPath("transportationType").type(STRING)
                                .description("대중교통 : 'PUBLIC' / 자동차 : 'PERSONAL'"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER)
                                .description("상태 코드"),
                        fieldWithPath("message").type(STRING)
                                .description("상태 메세지"),
                        fieldWithPath("data.locationName").type(STRING)
                                .description("출발 위치"),
                        fieldWithPath("data.transportation").type(STRING)
                                .description("내 이동수단"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("participate-update", prettyPrint(), prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("모임 나가기 API")
    @Test
    void participationExit() throws Exception {
        // given
        given(spaceService.participateExit(any(), any()))
                .willReturn(
                        SpaceExitResponse.builder()
                                .isDeletedSpace(false)
                                .message("모임에서 나갔습니다.")
                                .build()
                );

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.delete("/group/participate")
                .header(AUTHORIZATION, "Bearer {token}")
                .param("participateId", String.valueOf(1L));

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("스페이스 나가기 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .formParameters(
                        parameterWithName("participateId")
                                .description("참여자 정보 ID"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER)
                                .description("상태 코드"),
                        fieldWithPath("message").type(STRING)
                                .description("상태 메세지"),
                        fieldWithPath("data.isDeletedSpace").type(BOOLEAN)
                                .description("모임 삭제 여부 : 어드민이 나간경우 모임이 삭제 / 참가자가 나간경우 모임 나가기"),
                        fieldWithPath("data.message").type(STRING)
                                .description("모임이 삭제되었습니다. / 모임에서 나갔습니다."))
                .responseSchema(schema("GroupExitResponse"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("participation-exit", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("모임원 내보내기 API")
    @Test
    void participateRemoval() throws Exception {
        // given
        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.delete("/group/participate/removal")
                .header(AUTHORIZATION, "Bearer {token}")
                .param("participateId", String.valueOf(1L));

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("스페이스 내보내기 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .formParameters(
                        parameterWithName("participateId")
                                .description("참여자 정보 ID"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER)
                                .description("상태 코드"),
                        fieldWithPath("message").type(STRING)
                                .description("상태 메세지"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("participate-removal", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("모임 삭제 API")
    @Test
    void groupDelete() throws Exception {
        // given
        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.delete("/group")
                .header(AUTHORIZATION, "Bearer {token}")
                .param("groupId", String.valueOf(1L));

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("스페이스 삭제 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .formParameters(
                        parameterWithName("groupId")
                                .description("그룹 ID"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER)
                                .description("상태 코드"),
                        fieldWithPath("message").type(STRING)
                                .description("상태 메세지"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("group-delete", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("내 참여 정보 조회")
    @Test
    void getParticipationDetail() throws Exception {
        // given
        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/group/user")
                .header(AUTHORIZATION, "Bearer {token}")
                .param("groupId", String.valueOf(1L));
        SpaceParticipationsResponse participation =
                SpaceParticipationsResponse.toResponse(1L, "kim@naver.com", "김모임장", 37.4830372, 127.0133939, "서울 성북구 보문로34다길 2", "PUBLIC", true);

        given(spaceService.getParticipationDetail(anyLong(), any()))
                .willReturn(participation);

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("내 참여 정보 조회 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .queryParameters(
                        parameterWithName("groupId")
                                .description("그룹 ID"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.participationId").type(NUMBER).description("참여 ID"),
                        fieldWithPath("data.userEmail").type(STRING).description("유저 이메일"),
                        fieldWithPath("data.userName").type(STRING).description("유저 이름"),
                        fieldWithPath("data.latitude").type(NUMBER).description("유저 출발지 위도"),
                        fieldWithPath("data.longitude").type(NUMBER).description("유저 출발지 경도"),
                        fieldWithPath("data.locationName").type(STRING).description("유저 출발지 이름"),
                        fieldWithPath("data.transportation").type(STRING).description("유저 교통수단"),
                        fieldWithPath("data.isAdmin").type(BOOLEAN).description("관리자 여부(true: 모임장, false: 모임원)"))
                        .build();

        RestDocumentationResultHandler document =
                documentHandler("group-participation-detail", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("모임 추천 역(랜드마크) 조회하기 API")
    @Test
    void getBestRegion() throws Exception {
        List<PathDto> path = List.of(
                PathDto.builder().latitude(37.496592).longitude(126.862355).build(),
                PathDto.builder().latitude(37.496604).longitude(126.862604).build(),
                PathDto.builder().latitude(37.496699).longitude(126.863088).build(),
                PathDto.builder().latitude(37.496837).longitude(126.863448).build(),
                PathDto.builder().latitude(37.497112).longitude(126.863885).build(),
                PathDto.builder().latitude(37.499301).longitude(126.866645).build(),
                PathDto.builder().latitude(37.499421).longitude(126.866938).build(),
                PathDto.builder().latitude(37.499421).longitude(126.866938).build()
        );

        // given
        List<PlaceRouteResponse.MoveUserInfo> moveUserInfoList = List.of(
                new PlaceRouteResponse.MoveUserInfo(true, 1L, "김유정", PUBLIC, 2, 68, 15928.0, 1500, path),
                new PlaceRouteResponse.MoveUserInfo(false, 2L, "천현우", PUBLIC, 2, 96, 27725.0, 1500, path)
        );
        List<PlaceRouteResponse> placeRouteResponseList = List.of(
                new PlaceRouteResponse("안국", 37.576477, 126.985443, moveUserInfoList),
                new PlaceRouteResponse("경복궁(정부서울청사)", 37.575762, 126.97353, moveUserInfoList),
                new PlaceRouteResponse("광화문(세종문화회관)", 37.571525, 126.97717, moveUserInfoList)

        );
        given(spaceService.getBestRegion(any()))
                .willReturn(placeRouteResponseList);

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/group/best-region")
                .param("groupId", String.valueOf(14L));

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("스페이스 추천 역(랜드마크) 조회 API")
                .queryParameters(
                        parameterWithName("groupId")
                                .description("모이닷 스페이스 ID"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER)
                                .description("상태 코드"),
                        fieldWithPath("message").type(STRING)
                                .description("상태 메세지"),
                        fieldWithPath("data[].name").type(STRING)
                                .description("추천 지역 이름"),
                        fieldWithPath("data[].latitude").type(NUMBER)
                                .description("추천 지역 위도"),
                        fieldWithPath("data[].longitude").type(NUMBER)
                                .description("추천 지역 경도"),
                        fieldWithPath("data[].moveUserInfo[].isAdmin").type(BOOLEAN)
                                .description("모임장 여부(true: 모임장, false: 모임원)"),
                        fieldWithPath("data[].moveUserInfo[].userId").type(NUMBER)
                                .description("유저 아이디"),
                        fieldWithPath("data[].moveUserInfo[].userName").type(STRING)
                                .description("유저 이름"),
                        fieldWithPath("data[].moveUserInfo[].transportationType").type(STRING)
                                .description("유저 이동 수단"),
                        fieldWithPath("data[].moveUserInfo[].transitCount").type(NUMBER)
                                .description("유저 총 환승횟수"),
                        fieldWithPath("data[].moveUserInfo[].totalTime").type(NUMBER)
                                .description("유저 총 이동 시간(분)"),
                        fieldWithPath("data[].moveUserInfo[].transportationType").type(STRING)
                                .description("유저 이동 수단"),
                        fieldWithPath("data[].moveUserInfo[].totalDistance").type(NUMBER)
                                .description("유저 이동 거리(m)"),
                        fieldWithPath("data[].moveUserInfo[].payment").type(NUMBER)
                                .description("유저 이동 거리(원)"),
                        fieldWithPath("data[].moveUserInfo[].path[].x").type(NUMBER)
                                .description("경로(경도)"),
                        fieldWithPath("data[].moveUserInfo[].path[].y").type(NUMBER)
                                .description("경로(위도)"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("get-best-region", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("내 모임 확인하기 API")
    @Test
    void getMyParticipate() throws Exception {
        // given
        SpaceMyParticipateResponse data1 = SpaceMyParticipateResponse.builder()
                .groupId(1L)
                .groupName("그룹1")
                .groupAdminName("양파쿵야")
                .groupDate("2023-07-15")
                .groupParticipates(3)
                .confirmPlace("none")
                .isAdmin(true)
                .bestPlaceNames(List.of("종로5가역", "종로3가역", "동대문역"))
                .participantNames(List.of("양파쿵야", "주먹밥쿵야", "샐러리쿵야"))
                .build();

        SpaceMyParticipateResponse data2 = SpaceMyParticipateResponse.builder()
                .groupId(2L)
                .groupName("그룹2")
                .groupAdminName("주먹밥쿵야")
                .groupDate("2023-07-28")
                .groupParticipates(3)
                .confirmPlace("교대역")
                .isAdmin(false)
                .bestPlaceNames(List.of("강남역", "교대역", "역삼역"))
                .participantNames(List.of("양파쿵야", "주먹밥쿵야", "샐러리쿵야"))
                .build();

        given(spaceService.getMyParticipate(any(), any(), any()))
                .willReturn(List.of(data1, data2));

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/group/participate")
                .header(AUTHORIZATION, "Bearer {token}")
                .param("spaceName", "검색이름")
                .param("filter", "ABC");

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("내가 참여하고있는 스페이스 조회 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .queryParameters(
                        parameterWithName("spaceName").description("검색하고 싶은 장소 이름. 해당 이름 포함하는 스페이스 조회 가능."),
                        parameterWithName("filter").description("정렬 기준. ABC: 가나다순, LATEST: 최신순, OLDEST: 오래된 순")
                )
                .responseFields(
                        fieldWithPath("code").type(NUMBER)
                                .description("상태 코드"),
                        fieldWithPath("message").type(STRING)
                                .description("상태 메세지"),
                        fieldWithPath("data[].groupId").type(NUMBER)
                                .description("그룹 ID"),
                        fieldWithPath("data[].groupName").type(STRING)
                                .description("그룹 이름"),
                        fieldWithPath("data[].groupAdminName").type(STRING)
                                .description("그룹 모임장 이름"),
                        fieldWithPath("data[].groupDate").type(STRING)
                                .description("그룹 모임날짜"),
                        fieldWithPath("data[].groupParticipates").type(NUMBER)
                                .description("그룹 참여자 수 / Integer"),
                        fieldWithPath("data[].confirmPlace").type(STRING)
                                .description("그룹 확정 장소 / 미확정 : 'none' "),
                        fieldWithPath("data[].isAdmin").type(BOOLEAN)
                                .description("해당 그룹 모임장 여부"),
                        fieldWithPath("data[].participantNames[]").type(ARRAY)
                                .description("그룹 참여자 이름 리스트"),
                        fieldWithPath("data[].bestPlaceNames[]").type(ARRAY)
                                .description("그룹 추천장소 현황 리스트"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("my-participate", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("모임 장소 추천 조회 리스트 API")
    @Test
    void keywordCentralizedMeetingSpot() throws Exception {
        // given
        SpacePlaceResponse place1 = SpacePlaceResponse.builder()
                .title("멘야하나비 성신여대점")
                .thumUrl("https://ldb-phinf.pstatic.net/20230804_174/16911100078193yaWQ_JPEG/IMG_4118.JPEG")
                .distance("성신여대입구역(으)로부터 220m")
                .openTime("21:00에 라스트오더")
                .tel("02-6397-3020")
                .detail(
                        SpacePlaceResponse.Detail.builder()
                                .local("성신여대입구역")
                                .title("멘야하나비 성신여대점")
                                .address("서울특별시 성북구 동소문로22길 39-5 2층")
                                .status("영업 중")
                                .openTime("21:00에 라스트오더")
                                .homePageUrl("")
                                .tel("02-6397-3020")
                                .category(List.of("일식", "일본식라면"))
                                .x("127.0186061")
                                .y("37.5914413")
                                .thumUrls(List.of(
                                        "https://ldb-phinf.pstatic.net/20230804_174/16911100078193yaWQ_JPEG/IMG_4118.JPEG",
                                        "https://ldb-phinf.pstatic.net/20230804_278/1691110001584jSktH_JPEG/IMG_4117.JPEG",
                                        "https://ldb-phinf.pstatic.net/20230324_57/1679617040749YQEdq_JPEG/KakaoTalk_20230323_100524740.jpg"
                                ))
                                .menuInfo(List.of(
                                        "마제소바 11,000",
                                        "도니꾸 마제소바 14,000",
                                        "네기시오 마제소바 14,000",
                                        "스파이시 마제소바 12,000",
                                        "소유라멘 10,000",
                                        "카레마제소바 12,000"
                                ))
                                .build()
                )
                .build();

        SpacePlaceResponse place2 = SpacePlaceResponse.builder()
                .title("치치 성신여대점")
                .thumUrl("https://ldb-phinf.pstatic.net/20230704_152/1688449596232YkYod_JPEG/3.jpg")
                .distance("성신여대입구역(으)로부터 213m")
                .openTime("17:30에 영업시작")
                .tel("02-921-8520")
                .detail(
                        SpacePlaceResponse.Detail.builder()
                                .local("성신여대입구역")
                                .title("치치 성신여대점")
                                .address("서울특별시 성북구 동소문로20길 37-12 1층")
                                .status("곧 영업 시작")
                                .openTime("17:30에 영업시작")
                                .homePageUrl("http://www.chi-chi.co.kr/")
                                .tel("02-921-8520")
                                .category(List.of("술집", "요리주점"))
                                .x("127.0175747")
                                .y("37.5909647")
                                .thumUrls(List.of(
                                        "https://ldb-phinf.pstatic.net/20230704_152/1688449596232YkYod_JPEG/3.jpg",
                                        "https://ldb-phinf.pstatic.net/20230630_204/1688116794177JlDTz_JPEG/20230630_162032.jpg",
                                        "https://ldb-phinf.pstatic.net/20230704_54/16884496305868jaey_JPEG/1688297595651.jpg"
                                ))
                                .menuInfo(List.of(
                                        "버터갈릭감자 변동가격(업주문의)",
                                        "설탕토마토 변동가격(업주문의)"
                                ))
                                .build())
                .build();

        SpacePlaceResponse place3 = SpacePlaceResponse.builder()
                .title("동경산책 성신여대점")
                .thumUrl("https://ldb-phinf.pstatic.net/20220106_294/1641437440289J8dYW_JPEG/1635122589184-10.jpg")
                .distance("성신여대입구역(으)로부터 236m")
                .openTime("21:00에 영업종료")
                .tel("02-923-2666")
                .detail(
                        SpacePlaceResponse.Detail.builder()
                                .local("성신여대입구역")
                                .title("동경산책 성신여대점")
                                .address("서울특별시 성북구 보문로34길 45")
                                .status("영업 중")
                                .openTime("21:00에 영업종료")
                                .homePageUrl("http://www.instagram.com/dongkyungsancheck")
                                .tel("02-923-2666")
                                .category(List.of("일식", "일식당"))
                                .x("127.0179133")
                                .y("37.5908482")
                                .thumUrls(List.of(
                                        "https://ldb-phinf.pstatic.net/20220106_294/1641437440289J8dYW_JPEG/1635122589184-10.jpg",
                                        "https://ldb-phinf.pstatic.net/20220106_112/1641437356001DoVgV_JPEG/1540180782395.jpg",
                                        "https://ldb-phinf.pstatic.net/20220106_297/1641437421551RjALN_JPEG/IMG_20180602_101017_715.jpg"
                                ))
                                .menuInfo(List.of(
                                        "아나고사케동정식 변동가격(업주문의)",
                                        "스키야끼정식 변동가격(업주문의)"
                                ))
                                .build()
                )
                .build();

        given(spaceService.keywordCentralizedMeetingSpot(anyDouble(), anyDouble(), anyString(), anyString()))
                .willReturn(List.of(place1, place2, place3));

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/group/best-region/place")
                .param("x", "127.232943")
                .param("y", "37.6823811")
                .param("local", "성신여대입구역")
                .param("keyword", "식당");

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("스페이스 역(랜드마크)주변 추천 장소 조회 API")
                .queryParameters(
                        parameterWithName("x").description("역(또는 지역) x좌표"),
                        parameterWithName("y").description("역(또는 지역) y좌표"),
                        parameterWithName("local").description("역(또는 지역)이름"),
                        parameterWithName("keyword").description("카페 / 스터디카페 / 식당 / 도서관 / 스터디룸"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER)
                                .description("상태 코드"),
                        fieldWithPath("message").type(STRING)
                                .description("상태 메세지"),
                        fieldWithPath("data[].title").type(STRING)
                                .description("가게 이름"),
                        fieldWithPath("data[].thumUrl").type(STRING)
                                .description("썸네일 이미지 URL"),
                        fieldWithPath("data[].distance").type(STRING)
                                .description("거리"),
                        fieldWithPath("data[].openTime").type(STRING)
                                .description("영업 시간"),
                        fieldWithPath("data[].tel").type(STRING)
                                .description("전화번호"),
                        fieldWithPath("data[].detail.local").type(STRING)
                                .description("지역"),
                        fieldWithPath("data[].detail.title").type(STRING)
                                .description("가게 이름"),
                        fieldWithPath("data[].detail.address").type(STRING)
                                .description("주소"),
                        fieldWithPath("data[].detail.status").type(STRING)
                                .description("영업 상태"),
                        fieldWithPath("data[].detail.openTime").type(STRING)
                                .description("영업 시간"),
                        fieldWithPath("data[].detail.homePageUrl").type(STRING)
                                .description("홈페이지 URL"),
                        fieldWithPath("data[].detail.tel").type(STRING)
                                .description("전화번호"),
                        fieldWithPath("data[].detail.category[]").type(ARRAY)
                                .description("카테고리 목록 / List<String>"),
                        fieldWithPath("data[].detail.x").type(STRING)
                                .description("위도"),
                        fieldWithPath("data[].detail.y").type(STRING)
                                .description("경도"),
                        fieldWithPath("data[].detail.thumUrls[]").type(ARRAY)
                                .description("상세 이미지 URL 목록 / List<String>"),
                        fieldWithPath("data[].detail.menuInfo[]").type(ARRAY)
                                .description("메뉴 정보 목록 / List<String>"))
                .responseSchema(schema("GroupPlaceResponse"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("read-bestPlace-keyword", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("모임 참여자 정보 리스트 조회 API")
    @Test
    void readParticipateGroupByRegion() throws Exception {
        SpaceParticipationsResponse participations1 =
                SpaceParticipationsResponse.toResponse(1L, "kim@naver.com", "김모임장", 37.4830372, 127.0133939, "서울 성북구 보문로34다길 2", "PUBLIC", true);
        SpaceParticipationsResponse participations2 =
                SpaceParticipationsResponse.toResponse(2L, "park@naver.com", "박이람이", 37.4830372, 127.0133939, "서울 성북구 보문로34다길 2", "PUBLIC", false);
        SpaceRegionResponse region1 = SpaceRegionResponse.toResponse("서울 성북구", List.of(participations1, participations2));

        SpaceParticipationsResponse participations3 =
                SpaceParticipationsResponse.toResponse(3L, "cheon@gmail.com", "천수제비", 37.4830372, 127.0133939, "서울 강북구 도봉로 76가길 55", "PERSONAL", false);
        SpaceParticipationsResponse participations4 =
                SpaceParticipationsResponse.toResponse(4L, "moram@gmail.com", "모람모람", 37.4830372, 127.0133939, "서울 강북구 도봉로 76가길 54", "PUBLIC", false);
        SpaceRegionResponse region2 = SpaceRegionResponse.toResponse("서울 강북구", List.of(participations3, participations4));

        SpaceParticipationsResponse participations5 =
                SpaceParticipationsResponse.toResponse(3L, "enfp@gmail.com", "낭만 ENFP", 37.4830372, 127.0133939, "경기도 부천시 부천로 1", "PERSONAL", false);
        SpaceRegionResponse region3 =
                SpaceRegionResponse.toResponse("경기도 부천시", List.of(participations5));

        // given
        given(spaceService.readParticipateSpaceByRegion(anyLong()))
                .willReturn(
                        SpaceDetailResponse.builder()
                                .groupId(1L)
                                .adminEmail("kim@naver.com")
                                .name("모이닷 팀 프로젝트")
                                .date("2023-12-01")
                                .participantsByRegion(List.of(region1, region2, region3))
                                .build()
                );

        String participationsByRegion = "data.participantsByRegion[]";
        String participations = participationsByRegion + ".participations[]";

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/group")
                .param("groupId", "1");

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("스페이스 전체 참여자 조회 API")
                .queryParameters(
                        parameterWithName("groupId").description("그룹 Id"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.groupId").type(NUMBER).description("그룹 ID"),
                        fieldWithPath("data.adminEmail").type(STRING).description("그룹 어드민 이메일"),
                        fieldWithPath("data.name").type(STRING).description("그룹 이름"),
                        fieldWithPath("data.date").type(STRING).description("그룹 생성 날짜"),
                        fieldWithPath(participationsByRegion).type(ARRAY).description("그룹화된 지역 리스트"),
                        fieldWithPath(participationsByRegion + ".regionName").type(STRING).description("그룹화된 지역 이름"),
                        fieldWithPath(participations).type(ARRAY).description("그룹화된 지역 참여자 리스트"),
                        fieldWithPath(participations + ".participationId").type(NUMBER).description("참여 ID"),
                        fieldWithPath(participations + ".userEmail").type(STRING).description("유저 이메일"),
                        fieldWithPath(participations + ".userName").type(STRING).description("유저 이름"),
                        fieldWithPath(participations + ".latitude").type(NUMBER).description("유저 출발지 위도"),
                        fieldWithPath(participations + ".longitude").type(NUMBER).description("유저 출발지 경도"),
                        fieldWithPath(participations + ".locationName").type(STRING).description("유저 출발지 이름"),
                        fieldWithPath(participations + ".transportation").type(STRING).description("유저 교통수단"),
                        fieldWithPath(participations + ".isAdmin").type(BOOLEAN).description("관리자 여부(true: 모임장, false: 모임원)"))
                .responseSchema(schema("GroupDetailResponse"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("read-participate-region", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("모임 이름 수정 API")
    @Test
    void updateGroupName() throws Exception {
        // given
        SpaceNameUpdateRequest request = new SpaceNameUpdateRequest("모이닷런칭준비");
        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("스페이스 이름 수정 API")
                .queryParameters(
                        parameterWithName("groupId").description("그룹 Id"))
                .requestFields(
                        fieldWithPath("groupName").type(STRING).description("변경할 그룹 이름"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"))
                .responseSchema(schema("GroupDetailResponse"))
                .build();
        RestDocumentationResultHandler document = documentHandler("update-groupName", prettyPrint(), prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/group")
                                .header("Authorization", "JWT AccessToken")
                                .param("groupId", "1")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("닉네임 유효성 체크")
    @Test
    void checkNicknameValidation() throws Exception {
        // given
        given(spaceService.checkNicknameValidation(1L, "중복될일없는 특이한 닉네임"))
                .willReturn(new NicknameValidationResponse(false));

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("닉네임 유효성 체크 API")
                .queryParameters(
                        parameterWithName("groupId").description("그룹 Id"),
                        parameterWithName("nickname").description("닉네임"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.duplicated").type(BOOLEAN).description("닉네임 중복 여부"))
                .responseSchema(schema("NicknameValidationResponse"))
                .build();
        RestDocumentationResultHandler document = documentHandler("check-nickname-validation", prettyPrint(), prettyPrint(), parameters);

        // when // then
        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/group/nickname")
                .param("groupId", "1")
                .param("nickname", "중복될일없는 특이한 닉네임");

        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("모임 전체 나가기 API")
    @Test
    void allParticipateExit() throws Exception {
        // given
        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("스페이스 API")
                .summary("모임 전체 나가기 API")
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"))
                .build();

        RestDocumentationResultHandler document = documentHandler("all-participate-exit", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/group/participate/all")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }
}
