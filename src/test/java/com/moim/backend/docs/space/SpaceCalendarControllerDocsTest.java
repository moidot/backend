package com.moim.backend.docs.space;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.space.controller.SpaceCalendarController;
import com.moim.backend.domain.space.request.controller.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.response.space.CreateSpaceCalendarResponse;
import com.moim.backend.domain.space.service.SpaceCalendarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.time.LocalDateTime;

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
}
