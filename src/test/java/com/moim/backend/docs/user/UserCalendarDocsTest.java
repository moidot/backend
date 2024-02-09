package com.moim.backend.docs.user;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.user.controller.UserCalendarController;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
import com.moim.backend.domain.user.request.UserCalendarPageRequest;
import com.moim.backend.domain.user.response.CreateUserCalendarResponse;
import com.moim.backend.domain.user.response.UserCalendarPageResponse;
import com.moim.backend.domain.user.service.UserCalendarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moim.backend.domain.user.response.UserCalendarPageResponse.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserCalendarDocsTest extends RestDocsSupport {

    private final UserCalendarService userCalendarService = mock(UserCalendarService.class);

    @Override
    protected Object initController() {
        return new UserCalendarController(userCalendarService);
    }

    @DisplayName("개인 캘린더 일정 추가 API")
    @Test
    void createCalendar() throws Exception {
        // given
        CreateUserCalendarRequest request =
                new CreateUserCalendarRequest("모이닷 정기 오프라인 회의", LocalDateTime.of(2024, 1, 25, 15, 0, 0), "메모", "종로 2가", "R");

        given(userCalendarService.createUserCalendar(any(), any(CreateUserCalendarRequest.class)))
                .willReturn(
                        CreateUserCalendarResponse.builder()
                                .userCalendarId(1L)
                                .userId(1L)
                                .scheduleName("모이닷 정기 오프라인 회의")
                                .date("2023-01-25T15:00:00")
                                .dayOfWeek("목")
                                .note("메모")
                                .locationName("종로 2가")
                                .color("R")
                                .build()
                );

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("개인 캘린더 API")
                .summary("일정 추가하기 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .requestFields(
                        fieldWithPath("scheduleName").type(STRING).description("스케줄명"),
                        fieldWithPath("date").type(ARRAY).description("형식(String) : yyyy-MM-dd'T'HH:mm:ss"),
                        fieldWithPath("note").type(STRING).description("메모"),
                        fieldWithPath("locationName").type(STRING).description("장소 이름"),
                        fieldWithPath("color").type(STRING).description("색상"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.userCalendarId").type(NUMBER).description("개인 캘린더 ID"),
                        fieldWithPath("data.userId").type(NUMBER).description("유저 ID"),
                        fieldWithPath("data.scheduleName").type(STRING).description("스케줄명"),
                        fieldWithPath("data.date").type(STRING).description("날짜 및 시간 ex) '2024-01-24T15:36:15' "),
                        fieldWithPath("data.dayOfWeek").type(STRING).description("요일"),
                        fieldWithPath("data.note").type(STRING).description("메모"),
                        fieldWithPath("data.locationName").type(STRING).description("장소 이름"),
                        fieldWithPath("data.color").type(STRING).description("색상"))
                .build();

        // when // then
        RestDocumentationResultHandler document = documentHandler("create-userCalendar", prettyPrint(), prettyPrint(), parameters);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/auth/calendar")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("개인 캘린더 일정 추가 API")
    @Test
    void readCalendar() throws Exception {
        // given
        UserCalendarPageRequest request = new UserCalendarPageRequest(LocalDateTime.of(2024, 2, 1, 0, 0, 0));
        Map<Integer, List<Schedule>> defaultCalendar = getDefaultResponse();

        List<Schedule> schedules1 = new ArrayList<>();
        schedules1.add(Schedule.builder()
                .color("RED")
                .scheduleName("올리브영 갔다오기")
                .build());
        schedules1.add(Schedule.builder()
                .color("space")
                .scheduleName("모이닷 회의")
                .build());

        defaultCalendar.put(1, schedules1);

        given(userCalendarService.readMyCalendar(any(), any(UserCalendarPageRequest.class)))
                .willReturn(
                        builder()
                                .calendar(defaultCalendar)
                                .build()
                );

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("개인 캘린더 API")
                .summary("내 캘린더 조회 API")
                .description("요청시 date의 경우 배열 타입이 아닌 yyyy-MM-dd'T'HH:mm:ss 형식의 String으로 보내야 합니다")
                .requestFields(
                        fieldWithPath("date").type(ARRAY).description("형식(String) : yyyy-MM-dd'T'HH:mm:ss"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.calendar").type(OBJECT).description("캘린더"),
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.calendar").type(OBJECT).description("캘린더"),
                        fieldWithPath("data.calendar.1").type(ARRAY).description("1일의 일정 목록"),
                        fieldWithPath("data.calendar.1[].color").type(STRING).description("개인일정 - 색상 / 모임일정 - space"),
                        fieldWithPath("data.calendar.1[].scheduleName").type(STRING).description("스케줄명"),
                        fieldWithPath("data.calendar.2").type(ARRAY).description("2일의 일정 목록"),
                        fieldWithPath("data.calendar.3").type(ARRAY).description("3일의 일정 목록"),
                        fieldWithPath("data.calendar.4").type(ARRAY).description("4일의 일정 목록"),
                        fieldWithPath("data.calendar.5").type(ARRAY).description("5일의 일정 목록"),
                        fieldWithPath("data.calendar.6").type(ARRAY).description("6일의 일정 목록"),
                        fieldWithPath("data.calendar.7").type(ARRAY).description("7일의 일정 목록"),
                        fieldWithPath("data.calendar.8").type(ARRAY).description("8일의 일정 목록"),
                        fieldWithPath("data.calendar.9").type(ARRAY).description("9일의 일정 목록"),
                        fieldWithPath("data.calendar.10").type(ARRAY).description("10일의 일정 목록"),
                        fieldWithPath("data.calendar.11").type(ARRAY).description("11일의 일정 목록"),
                        fieldWithPath("data.calendar.12").type(ARRAY).description("12일의 일정 목록"),
                        fieldWithPath("data.calendar.13").type(ARRAY).description("13일의 일정 목록"),
                        fieldWithPath("data.calendar.14").type(ARRAY).description("14일의 일정 목록"),
                        fieldWithPath("data.calendar.15").type(ARRAY).description("15일의 일정 목록"),
                        fieldWithPath("data.calendar.16").type(ARRAY).description("16일의 일정 목록"),
                        fieldWithPath("data.calendar.17").type(ARRAY).description("17일의 일정 목록"),
                        fieldWithPath("data.calendar.18").type(ARRAY).description("18일의 일정 목록"),
                        fieldWithPath("data.calendar.19").type(ARRAY).description("19일의 일정 목록"),
                        fieldWithPath("data.calendar.20").type(ARRAY).description("20일의 일정 목록"),
                        fieldWithPath("data.calendar.21").type(ARRAY).description("21일의 일정 목록"),
                        fieldWithPath("data.calendar.22").type(ARRAY).description("22일의 일정 목록"),
                        fieldWithPath("data.calendar.23").type(ARRAY).description("23일의 일정 목록"),
                        fieldWithPath("data.calendar.24").type(ARRAY).description("24일의 일정 목록"),
                        fieldWithPath("data.calendar.25").type(ARRAY).description("25일의 일정 목록"),
                        fieldWithPath("data.calendar.26").type(ARRAY).description("26일의 일정 목록"),
                        fieldWithPath("data.calendar.27").type(ARRAY).description("27일의 일정 목록"),
                        fieldWithPath("data.calendar.28").type(ARRAY).description("28일의 일정 목록"),
                        fieldWithPath("data.calendar.29").type(ARRAY).description("29일의 일정 목록"),
                        fieldWithPath("data.calendar.30").type(ARRAY).description("30일의 일정 목록"),
                        fieldWithPath("data.calendar.31").type(ARRAY).description("31일의 일정 목록"))
                .build();

        RestDocumentationResultHandler document = documentHandler("read-calendar", prettyPrint(), prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/auth/calendar")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    private static Map<Integer, List<Schedule>> getDefaultResponse() {
        Map<Integer, List<Schedule>> response = new HashMap<>();
        for (int day = 1; day <= 31; day++) {
            response.put(day, new ArrayList<>());
        }
        return response;
    }
}
