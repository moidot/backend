package com.moim.backend.docs.space;

import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.space.controller.GroupController;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.request.GroupRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.space.response.PathDto;
import com.moim.backend.domain.space.response.PlaceRouteResponse;
import com.moim.backend.domain.space.service.GroupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
                                fieldWithPath("name").type(STRING)
                                        .description("모임 이름"),
                                fieldWithPath("date").type(STRING)
                                        .description("모임 날짜 / 'yyyy-dd-mm'")
                                        .optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.groupId").type(NUMBER)
                                        .description("모임 ID / Long"),
                                fieldWithPath("data.adminId").type(NUMBER)
                                        .description("모임장 ID / Long"),
                                fieldWithPath("data.name").type(STRING)
                                        .description("모임 이름"),
                                fieldWithPath("data.date").type(STRING)
                                        .description("모임 날짜"),
                                fieldWithPath("data.fixedPlace").type(STRING)
                                        .description("확정 장소")
                        )
                ));
    }

    @DisplayName("모임 참여 API")
    @Test
    void participationGroup() throws Exception {
        // given
        GroupRequest.Participate request
                = new GroupRequest.Participate(1L, "안지영", "쇼파르", 37.5660, 126.9784, TransportationType.PUBLIC, "123456");

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
                                .transportation("PUBLIC")
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
                                fieldWithPath("groupId").type(NUMBER)
                                        .description("그룹 ID / Long"),
                                fieldWithPath("userName").type(STRING)
                                        .description("유저 닉네임"),
                                fieldWithPath("locationName").type(STRING)
                                        .description("출발 위치 이름"),
                                fieldWithPath("latitude").type(NUMBER)
                                        .description("위도 / Double"),
                                fieldWithPath("longitude").type(NUMBER)
                                        .description("경도 / Double"),
                                fieldWithPath("transportationType").type(STRING)
                                        .description("대중교통 : 'PUBLIC' / 자동차 : 'PERSONAL'"),
                                fieldWithPath("password").type(STRING)
                                        .optional()
                                        .description("모임 내 비밀번호")
                        ),
                        responseFields(
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
                                        .description("내 이동수단")
                        )
                ));
    }

    @DisplayName("내 참여 정보 수정 API")
    @Test
    void participationUpdate() throws Exception {
        // given
        GroupRequest.ParticipateUpdate request
                = new GroupRequest.ParticipateUpdate(1L, "양파쿵야", "쇼파르", 37.5660, 126.9784, TransportationType.PERSONAL);

        given(groupService.participateUpdate(any(), any()))
                .willReturn(
                        GroupResponse.ParticipateUpdate.builder()
                                .locationName("쇼파르")
                                .transportation("PERSONAL")
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
                                        .description("대중교통 : 'PUBLIC' / 자동차 : 'PERSONAL'")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.locationName").type(STRING)
                                        .description("출발 위치"),
                                fieldWithPath("data.transportation").type(STRING)
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
                                fieldWithPath("code").type(NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.isDeletedSpace").type(JsonFieldType.BOOLEAN)
                                        .description("모임 삭제 여부 : 어드민이 나간경우 모임이 삭제 / 참가자가 나간경우 모임 나가기"),
                                fieldWithPath("data.message").type(STRING)
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
                                fieldWithPath("code").type(NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(STRING)
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
                new PlaceRouteResponse.MoveUserInfo(1L, "김유정", TransportationType.PUBLIC, 2, 68, 15928.0, path),
                new PlaceRouteResponse.MoveUserInfo(2L, "천현우", TransportationType.PUBLIC, 2, 96, 27725.0, path)
        );
        List<PlaceRouteResponse> placeRouteResponseList = List.of(
                new PlaceRouteResponse("안국", 37.576477, 126.985443, moveUserInfoList),
                new PlaceRouteResponse("경복궁(정부서울청사)", 37.575762, 126.97353, moveUserInfoList),
                new PlaceRouteResponse("광화문(세종문화회관)", 37.571525, 126.97717, moveUserInfoList)

        );
        given(groupService.getBestRegion(any()))
                .willReturn(placeRouteResponseList);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/group/best-region")
                                .param("groupId", String.valueOf(1L))
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
                                fieldWithPath("code").type(NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .description("추천 지역 이름"),
                                fieldWithPath("data[].latitude").type(JsonFieldType.NUMBER)
                                        .description("추천 지역 위도"),
                                fieldWithPath("data[].longitude").type(JsonFieldType.NUMBER)
                                        .description("추천 지역 경도"),
                                fieldWithPath("data[].moveUserInfo[].userId").type(JsonFieldType.NUMBER)
                                        .description("유저 아이디"),
                                fieldWithPath("data[].moveUserInfo[].userName").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data[].moveUserInfo[].transportationType").type(JsonFieldType.STRING)
                                        .description("유저 이동 수단"),
                                fieldWithPath("data[].moveUserInfo[].transitCount").type(JsonFieldType.NUMBER)
                                        .description("유저 총 환승횟수"),
                                fieldWithPath("data[].moveUserInfo[].totalTime").type(JsonFieldType.NUMBER)
                                        .description("유저 총 이동 시간(분)"),
                                fieldWithPath("data[].moveUserInfo[].transportationType").type(JsonFieldType.STRING)
                                        .description("유저 이동 수단"),
                                fieldWithPath("data[].moveUserInfo[].totalDistance").type(JsonFieldType.NUMBER)
                                        .description("유저 이동 거리(m)"),
                                fieldWithPath("data[].moveUserInfo[].path[].x").type(JsonFieldType.NUMBER)
                                        .description("경로(경도)"),
                                fieldWithPath("data[].moveUserInfo[].path[].y").type(JsonFieldType.NUMBER)
                                        .description("경로(위도)")
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
                                fieldWithPath("code").type(NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data[].groupId").type(NUMBER)
                                        .description("그룹 ID"),
                                fieldWithPath("data[].groupName").type(STRING)
                                        .description("그룹 이름"),
                                fieldWithPath("data[].groupDate").type(STRING)
                                        .description("그룹 모임날짜"),
                                fieldWithPath("data[].groupParticipates").type(NUMBER)
                                        .description("그룹 참여자 수 / Integer"),
                                fieldWithPath("data[].bestPlaces[]").type(JsonFieldType.ARRAY)
                                        .description("그룹 추천장소 현황"),
                                fieldWithPath("data[].bestPlaces[].bestPlaceId").type(NUMBER)
                                        .description("그룹 추천장소 ID / Long"),
                                fieldWithPath("data[].bestPlaces[].bestPlaceName").type(STRING)
                                        .description("그룹 추천장소 이름")
                        )
                ));
    }

    @DisplayName("모임 장소 추천 조회 리스트 API")
    @Test
    void keywordCentralizedMeetingSpot() throws Exception {
        // given
        GroupResponse.Place place1 = GroupResponse.Place.builder()
                .title("멘야하나비 성신여대점")
                .thumUrl("https://ldb-phinf.pstatic.net/20230804_174/16911100078193yaWQ_JPEG/IMG_4118.JPEG")
                .distance("성신여대입구역(으)로부터 220m")
                .openTime("21:00에 라스트오더")
                .tel("02-6397-3020")
                .detail(
                        GroupResponse.Place.Detail.builder()
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

        GroupResponse.Place place2 = GroupResponse.Place.builder()
                .title("치치 성신여대점")
                .thumUrl("https://ldb-phinf.pstatic.net/20230704_152/1688449596232YkYod_JPEG/3.jpg")
                .distance("성신여대입구역(으)로부터 213m")
                .openTime("17:30에 영업시작")
                .tel("02-921-8520")
                .detail(
                        GroupResponse.Place.Detail.builder()
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
                                .build()
                )
                .build();

        GroupResponse.Place place3 = GroupResponse.Place.builder()
                .title("동경산책 성신여대점")
                .thumUrl("https://ldb-phinf.pstatic.net/20220106_294/1641437440289J8dYW_JPEG/1635122589184-10.jpg")
                .distance("성신여대입구역(으)로부터 236m")
                .openTime("21:00에 영업종료")
                .tel("02-923-2666")
                .detail(
                        GroupResponse.Place.Detail.builder()
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

        given(groupService.keywordCentralizedMeetingSpot(anyDouble(), anyDouble(), anyString(), anyString()))
                .willReturn(List.of(place1, place2, place3));

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/group/best-region/place")
                                .param("x", "127.232943")
                                .param("y", "37.6823811")
                                .param("local", "성신여대입구역")
                                .param("keyword", "식당")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("read-bestPlace-keyword",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("x").description("역(또는 지역) x좌표"),
                                parameterWithName("y").description("역(또는 지역) y좌표"),
                                parameterWithName("local").description("역(또는 지역)이름"),
                                parameterWithName("keyword").description("카페 / 스터디카페 / 식당 / 도서관 / 스터디룸")
                        ),
                        responseFields(
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
                                fieldWithPath("data[].detail.category[]").type(JsonFieldType.ARRAY)
                                        .description("카테고리 목록 / List<String>"),
                                fieldWithPath("data[].detail.x").type(STRING)
                                        .description("위도"),
                                fieldWithPath("data[].detail.y").type(STRING)
                                        .description("경도"),
                                fieldWithPath("data[].detail.thumUrls[]").type(JsonFieldType.ARRAY)
                                        .description("상세 이미지 URL 목록 / List<String>"),
                                fieldWithPath("data[].detail.menuInfo[]").type(JsonFieldType.ARRAY)
                                        .description("메뉴 정보 목록 / List<String>")
                        )
                ));
    }

    @DisplayName("모임 참여자 정보 리스트 조회 API")
    @Test
    void readParticipateGroupByRegion() throws Exception {
        GroupResponse.Participations participations1 = new GroupResponse.Participations(1L, 1L, "김모임장", "서울 성북구 보문로34다길 2", "PUBLIC");
        GroupResponse.Participations participations2 = new GroupResponse.Participations(2L, 13L, "박이람이", "서울 성북구 보문로34다길 2", "PUBLIC");
        GroupResponse.Region region1 = new GroupResponse.Region("서울 성북구", List.of(participations1, participations2));

        GroupResponse.Participations participations3 = new GroupResponse.Participations(3L, 25L, "천수제비", "서울 강북구 도봉로 76가길 55", "PERSONAL");
        GroupResponse.Participations participations4 = new GroupResponse.Participations(4L, 6L, "모람모람", "서울 강북구 도봉로 76가길 54", "PUBLIC");
        GroupResponse.Region region2 = new GroupResponse.Region("서울 강북구", List.of(participations3, participations4));

        GroupResponse.Participations participations5 = new GroupResponse.Participations(3L, 25L, "낭만 ENFP", "경기도 부천시 부천로 1", "PERSONAL");
        GroupResponse.Region region3 = new GroupResponse.Region("경기도 부천시", List.of(participations5));

        // given
        given(groupService.readParticipateGroupByRegion(anyLong()))
                .willReturn(
                        GroupResponse.Detail.builder()
                                .groupId(1L)
                                .adminId(1L)
                                .name("모이닷 팀 프로젝트")
                                .date("2023-12-01")
                                .participantsByRegion(List.of(region1,region2,region3))
                                .build()
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/group")
                                .param("groupId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("read-participate-region",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("groupId").description("그룹 Id")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.groupId").type(NUMBER)
                                        .description("그룹 ID"),
                                fieldWithPath("data.adminId").type(NUMBER)
                                        .description("그룹 어드민 ID"),
                                fieldWithPath("data.name").type(STRING)
                                        .description("그룹 이름"),
                                fieldWithPath("data.date").type(STRING)
                                        .description("그룹 생성 날짜"),
                                fieldWithPath("data.participantsByRegion[]").type(ARRAY)
                                        .description("그룹화된 지역 리스트"),
                                fieldWithPath("data.participantsByRegion[].regionName").type(STRING)
                                        .description("그룹화된 지역 이름"),
                                fieldWithPath("data.participantsByRegion[].participations[]").type(ARRAY)
                                        .description("그룹화된 지역 참여자 리스트"),
                                fieldWithPath("data.participantsByRegion[].participations[].participationId").type(NUMBER)
                                        .description("참여 ID"),
                                fieldWithPath("data.participantsByRegion[].participations[].userId").type(NUMBER)
                                        .description("유저 ID"),
                                fieldWithPath("data.participantsByRegion[].participations[].userName").type(STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data.participantsByRegion[].participations[].locationName").type(STRING)
                                        .description("유저 출발지 이름"),
                                fieldWithPath("data.participantsByRegion[].participations[].transportation").type(STRING)
                                        .description("유저 교통수단")
                        ))
                );
    }
}
