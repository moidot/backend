package com.moim.backend.docs.space;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.space.controller.SpaceCalendarController;
import com.moim.backend.domain.space.request.controller.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.request.controller.SpaceTimeLineRequest;
import com.moim.backend.domain.space.response.space.CreateSpaceCalendarResponse;
import com.moim.backend.domain.space.response.space.SpaceTimeLineResponse;
import com.moim.backend.domain.space.service.SpaceCalendarService;
import com.moim.backend.global.util.DateParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static com.moim.backend.domain.space.response.space.SpaceTimeLineResponse.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpaceCalendarControllerDocsTest extends RestDocsSupport {

    private final SpaceCalendarService spaceCalendarService = mock(SpaceCalendarService.class);

    @Override
    protected Object initController() {
        return new SpaceCalendarController(spaceCalendarService);
    }

    @DisplayName("모임 캘린더 일정 추가 API")
    @Test
    void createCalendar() throws Exception {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 2, 15, 37, 32);
        CreateSpaceCalendarRequest request =
                new CreateSpaceCalendarRequest(1L, "테스트 입니다", localDateTime, "테스트", "안산시 성포동");


        given(spaceCalendarService.createSpaceCalendar(any(CreateSpaceCalendarRequest.class)))
                .willReturn(
                        CreateSpaceCalendarResponse.builder()
                                .spaceCalendarId(1L)
                                .spaceId(1L)
                                .scheduleName("테스트 입니다")
                                .date("2024-02-07T15:36:32")
                                .dayOfWeek("수")
                                .note("안녕하세요")
                                .locationName("안산시 상록구 예술광장 1로")
                                .build());

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("모임 캘린더 API")
                .summary("모임 캘린더 생성 API")
                .requestFields(
                        fieldWithPath("spaceId").type(NUMBER).description("스페이스 ID"),
                        fieldWithPath("scheduleName").type(STRING).description("스케줄명"),
                        fieldWithPath("date").type(ARRAY).description("형식 : yyyy-MM-dd'T'HH:mm:ss"),
                        fieldWithPath("note").type(STRING).description("메모"),
                        fieldWithPath("locationName").type(STRING).description("장소 이름"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.spaceCalendarId").type(NUMBER).description("개인 캘린더 ID"),
                        fieldWithPath("data.spaceId").type(NUMBER).description("유저 ID"),
                        fieldWithPath("data.scheduleName").type(STRING).description("스케줄명"),
                        fieldWithPath("data.date").type(STRING).description("날짜 및 시간 ex) '2024-01-24T15:36:15' "),
                        fieldWithPath("data.dayOfWeek").type(STRING).description("요일"),
                        fieldWithPath("data.note").type(STRING).description("메모"),
                        fieldWithPath("data.locationName").type(STRING).description("장소 이름"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("create-space-calendar", prettyPrint(), prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/space/calendar")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("타임라인 조회 API")
    @Test
    void readSpaceTimeLine() throws Exception {
        // given
        SpaceTimeLineRequest request = new SpaceTimeLineRequest(1L, LocalDateTime.of(2024, 2, 1, 0, 0, 0));
        Map<String, List<Schedule>> timeLine = getDefaultTimeLine();

        List<Schedule> schedules = new ArrayList<>();
        schedules.add(Schedule.builder()
                .scheduleName("안드로이드 회의")
                .date("오전 11:00")
                .build());
        schedules.add(Schedule.builder()
                .scheduleName("모이닷 회의")
                .date("오후 10:00")
                .build());

        timeLine.put("1/목", schedules);

        given(spaceCalendarService.readSpaceTimeLine(any(SpaceTimeLineRequest.class)))
                .willReturn(builder()
                        .timeLine(timeLine)
                        .build()
                );

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("모임 캘린더 API")
                .summary("타임라인 조회 API")
                .description("date 요청 시 String으로 보내야 합니다. ex) 2024-02-01T00:00:00 / 해당 응답 예시는 2024년 2월 기준입니다.")
                .requestFields(
                        fieldWithPath("spaceId").type(NUMBER).description("스페이스 ID"),
                        fieldWithPath("date").type(ARRAY).description("조회하려는 달의 1일 0시 0분으로 보내주세요!"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.timeLine").type(OBJECT).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.1/목").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.1/목[].date").type(STRING).description("날짜 ex) 1/목"),
                        fieldWithPath("data.timeLine.1/목[].scheduleName").type(STRING).description("스케줄 이름"),
                        fieldWithPath("data.timeLine.2/금").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.3/토").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.4/일").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.5/월").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.6/화").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.7/수").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.8/목").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.9/금").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.10/토").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.11/일").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.12/월").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.13/화").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.14/수").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.15/목").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.16/금").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.17/토").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.18/일").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.19/월").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.20/화").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.21/수").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.22/목").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.23/금").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.24/토").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.25/일").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.26/월").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.27/화").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.28/수").type(ARRAY).description("모임 스케줄 리스트"),
                        fieldWithPath("data.timeLine.29/목").type(ARRAY).description("모임 스케줄 리스트"))
                .build();

        RestDocumentationResultHandler document = documentHandler("read-timeLine", prettyPrint(), prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/space/timeLine")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    private static Map<String, List<Schedule>> getDefaultTimeLine() {
        Map<String, List<Schedule>> timeLine = new LinkedHashMap<>();

        for (int nextDay = 0; nextDay < 29; nextDay++) {
            LocalDateTime day = LocalDateTime.of(2024, 2, 1, 0, 0, 0).plusDays(nextDay);
            String dayOfWeek = DateParser.getDayOfWeek(day);
            String date = day.getDayOfMonth() + "/" + dayOfWeek;
            System.out.println(date);
            timeLine.put(date, new ArrayList<>());
        }

        return timeLine;
    }
}
