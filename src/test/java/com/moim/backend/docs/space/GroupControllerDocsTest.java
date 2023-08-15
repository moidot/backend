package com.moim.backend.docs.space;

import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.space.controller.GroupController;
import com.moim.backend.domain.space.request.GroupRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.space.response.KakaoMapDetailDto;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
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
                                        .description("유저 닉네임"),
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
                                        .description("유저 닉네임"),
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
                                        .description("유저 닉네임"),
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

    @DisplayName("추천된 장소 상세보기 API")
    @Test
    void RecommendedPlaceDetails() throws Exception {
        // given
        List<KakaoMapDetailDto.TimeList> timeList = List.of(new KakaoMapDetailDto.TimeList("영업시간", "09:30 ~ 21:00", "매일"));
        List<String> tags = List.of("스터디카페", "제로페이");
        List<GroupResponse.Photos> photos = List.of(
                new GroupResponse.Photos("M", "http://t1.kakaocdn.net/fiy_reboot/place/D02C1C1162A548B58894B236B754CFD6"),
                new GroupResponse.Photos("R2", "http://t1.kakaocdn.net/fiy_reboot/place/7F10166652F94503872F7A8B07A55F42"),
                new GroupResponse.Photos("R3", "http://t1.kakaocdn.net/fiy_reboot/place/5B14072B5D18448C81CF530950F0F571"),
                new GroupResponse.Photos("R4", "http://t1.kakaocdn.net/fiy_reboot/place/8BC6FCB8CCEC45B1804B3557E72F8721"),
                new GroupResponse.Photos("R5", "http://t1.kakaocdn.net/fiy_reboot/place/B2F2340363F44F18A76773AB8CADAC72")
        );

        List<KakaoMapDetailDto.MenuList> menuLists = List.of(
                new KakaoMapDetailDto.MenuList("3,300", false, "아메리카노"),
                new KakaoMapDetailDto.MenuList("3,800", false, "카페라떼"),
                new KakaoMapDetailDto.MenuList("3,800", false, "자몽에이드"),
                new KakaoMapDetailDto.MenuList("4,000", false, "유자차"),
                new KakaoMapDetailDto.MenuList("4,000", false, "자몽차")
        );

        KakaoMapDetailDto.MenuInfo menuInfo = new KakaoMapDetailDto.MenuInfo(5, menuLists, "N", 0, "2022.11.11.");

        given(groupService.detailRecommendedPlace(anyLong()))
                .willReturn(
                        GroupResponse.detailRecommendedPlace.builder()
                                .placeName("커피나무 성신여대점")
                                .mainPhotoUrl("http://t1.kakaocdn.net/fiy_reboot/place/D02C1C1162A548B58894B236B754CFD6")
                                .detailPlace("서울 성북구 보문로30길 79 1, 2층")
                                .isOpen("영업중")
                                .openTime(timeList)
                                .url("http://www.coffeenamu.co.kr")
                                .phone("02-922-1672")
                                .tags(tags)
                                .delivery("배달불가")
                                .pagekage("포장가능")
                                .photos(photos)
                                .menuInfo(menuInfo)
                                .build()
                );
        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/group/{id}", 26974293L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("detail-recommended",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id")
                                        .description("카카오맵의 추천장소 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.placeName").type(JsonFieldType.STRING)
                                        .description("장소 이름"),
                                fieldWithPath("data.mainPhotoUrl").type(JsonFieldType.STRING)
                                        .description("장소 사진"),
                                fieldWithPath("data.detailPlace").type(JsonFieldType.STRING)
                                        .description("서울 성북구 보문로30길 79 1, 2층"),
                                fieldWithPath("data.isOpen").type(JsonFieldType.STRING)
                                        .description("현재 영업 여부"),
                                fieldWithPath("data.openTime[].timeName").type(JsonFieldType.STRING)
                                        .description("영업 시간 이름"),
                                fieldWithPath("data.openTime[].timeSE").type(JsonFieldType.STRING)
                                        .description("영업 시간"),
                                fieldWithPath("data.openTime[].dayOfWeek").type(JsonFieldType.STRING)
                                        .description("해당 요일"),
                                fieldWithPath("data.url").type(JsonFieldType.STRING)
                                        .description("장소 홈페이지 URL"),
                                fieldWithPath("data.phone").type(JsonFieldType.STRING)
                                        .description("장소 대표번호"),
                                fieldWithPath("data.tags[]").type(JsonFieldType.ARRAY)
                                        .description("장소 특징 / StringArray"),
                                fieldWithPath("data.delivery").type(JsonFieldType.STRING)
                                        .description("배달 가능 여부"),
                                fieldWithPath("data.pagekage").type(JsonFieldType.STRING)
                                        .description("포장 가능 여부"),
                                fieldWithPath("data.photos[].photoId").type(JsonFieldType.STRING)
                                        .description("카카오맵에서 사진 메인 및 사이드 여부"),
                                fieldWithPath("data.photos[].photoUrl").type(JsonFieldType.STRING)
                                        .description("사진 URL"),
                                fieldWithPath("data.menuInfo.menucount").type(JsonFieldType.NUMBER)
                                        .description("메뉴 개수"),
                                fieldWithPath("data.menuInfo.menuList[].price").type(JsonFieldType.STRING)
                                        .description("메뉴 가격"),
                                fieldWithPath("data.menuInfo.menuList[].recommend").type(JsonFieldType.BOOLEAN)
                                        .description("메뉴 추천 여부"),
                                fieldWithPath("data.menuInfo.menuList[].menu").type(JsonFieldType.STRING)
                                        .description("메뉴 이름"),
                                fieldWithPath("data.menuInfo.productyn").type(JsonFieldType.STRING)
                                        .description("상품 확인 여부"),
                                fieldWithPath("data.menuInfo.menuboardphotocount").type(JsonFieldType.NUMBER)
                                        .description("메뉴판 사진 개수"),
                                fieldWithPath("data.menuInfo.timeexp").type(JsonFieldType.STRING)
                                        .description("메뉴 갱신 날짜")
                        )
                ));
    }

    @DisplayName("")
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
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING)
                                        .description("가게 이름"),
                                fieldWithPath("data[].thumUrl").type(JsonFieldType.STRING)
                                        .description("썸네일 이미지 URL"),
                                fieldWithPath("data[].distance").type(JsonFieldType.STRING)
                                        .description("거리"),
                                fieldWithPath("data[].openTime").type(JsonFieldType.STRING)
                                        .description("영업 시간"),
                                fieldWithPath("data[].tel").type(JsonFieldType.STRING)
                                        .description("전화번호"),
                                fieldWithPath("data[].detail.local").type(JsonFieldType.STRING)
                                        .description("지역"),
                                fieldWithPath("data[].detail.title").type(JsonFieldType.STRING)
                                        .description("가게 이름"),
                                fieldWithPath("data[].detail.address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("data[].detail.status").type(JsonFieldType.STRING)
                                        .description("영업 상태"),
                                fieldWithPath("data[].detail.openTime").type(JsonFieldType.STRING)
                                        .description("영업 시간"),
                                fieldWithPath("data[].detail.homePageUrl").type(JsonFieldType.STRING)
                                        .description("홈페이지 URL"),
                                fieldWithPath("data[].detail.tel").type(JsonFieldType.STRING)
                                        .description("전화번호"),
                                fieldWithPath("data[].detail.category[]").type(JsonFieldType.ARRAY)
                                        .description("카테고리 목록 / List<String>"),
                                fieldWithPath("data[].detail.x").type(JsonFieldType.STRING)
                                        .description("위도"),
                                fieldWithPath("data[].detail.y").type(JsonFieldType.STRING)
                                        .description("경도"),
                                fieldWithPath("data[].detail.thumUrls[]").type(JsonFieldType.ARRAY)
                                        .description("상세 이미지 URL 목록 / List<String>"),
                                fieldWithPath("data[].detail.menuInfo[]").type(JsonFieldType.ARRAY)
                                        .description("메뉴 정보 목록 / List<String>")
                                )
                ));
    }
}
