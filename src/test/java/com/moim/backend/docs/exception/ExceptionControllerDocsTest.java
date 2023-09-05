package com.moim.backend.docs.exception;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.exception.ExceptionController;
import com.moim.backend.domain.exception.ExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.moim.backend.global.common.Result.NOT_FOUND_GROUP;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExceptionControllerDocsTest extends RestDocsSupport {

    private final ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);

    @Override
    protected Object initController() {
        return new ExceptionController(exceptionHandler);
    }

    @DisplayName("기본 에러코드 문서")
    @Test
    void commonExceptionDocs() throws Exception {
        // given
        given(exceptionHandler.commonException())
                .willReturn("email은 null이 될 수 없습니다.");

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("Exception")
                .summary("Parameter Exception")
                .description("""
                        parameter가 올바르지 않을 때 발생
                        """
                )
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("exception1", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/common-exception")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document);
    }

    @DisplayName("커스텀 에러코드 문서")
    @Test
    void customExceptionDocs() throws Exception {
        // given
        given(exceptionHandler.customException())
                .willReturn(NOT_FOUND_GROUP);

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("Exception")
                .summary("Custom Exception")
                .description("""
                        상태 코드 / 상태 메시지 \s
                        \s
                        -900, 프로필을 요청하기위한 액세스 정보가 유효하지 않습니다. \s
                        -901, 네이버 로그인을 위한 접근 URL이 잘못되었습니다. \s
                        -902, "토큰 정보 접근을 위한 권한이 잘못되었습니다. \s
                        \s
                        -500, 예상치 못한 예외가 발생했습니다. \s
                        \s
                        -1001,"존재하지 않는 그룹입니다. \s
                        -1002, 잘못된 이동수단 입니다. \s
                        -1003, 존재하지 않는 참여자 정보 입니다. \s
                        -1004, 자신의 참여 정보가 아닙니다. \s
                        -1005, 해당 유저는 그룹의 어드민이 아닙니다. \s
                        -1006, 동일한 유저가 이미 스페이스에 참여하고 있습니다. \s
                        -1007, 네이버 API 요청에 실패하였습니다. \s
                        -1008, 현재 참여하고 있는 모임이 존재하지 않습니다. \s
                        -1009, 잘못된 지역 이름 입니다. \s
                        \s
                        -2001, 해당 그룹은 투표가 개설되지 않았습니다. \s
                        -2002, 존재하지 않는 추천 장소 입니다. \s
                        -2003, 해당 투표는 이미 종료되었습니다. \s
                        -2004, 해당 투표는 중복 선택이 허용되지 않습니다. \s
                        -2005, 해당 투표는 종료 시간이 지났습니다. \s
                        -2006, 해당 장소를 투표한 인원은 0명 입니다. \s
                        -2007, "해당 모임은 이미 투표가 시작되었습니다. \s
                        """
                )
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("exception2", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/custom-exception")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document);
    }
}
