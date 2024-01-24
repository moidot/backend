package com.moim.backend.docs.user;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.user.controller.UserCalendarController;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
import com.moim.backend.domain.user.response.CreateUserCalendarResponse;
import com.moim.backend.domain.user.service.UserCalendarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

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

        given(userCalendarService.createUserCalendar(any(),any(CreateUserCalendarRequest.class)))
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
                        fieldWithPath("date").type(ARRAY).description("형식 : yyyy-MM-dd'T'HH:mm:ss"),
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
}
