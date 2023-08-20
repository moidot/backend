package com.moim.backend.docs.user;

import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.controller.UserController;
import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.domain.user.response.UserResponse;
import com.moim.backend.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.moim.backend.domain.user.config.Platform.KAKAO;
import static com.moim.backend.domain.user.config.Platform.NAVER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);
    private final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5dS1qdW5nMzE0NzZAbmF2ZXIuY29tIiwiZXhwIjoxNjg5MjYwODM2fQ.cgZ8eFDU_Gz7Z3EghXxoa3v-iXUeQmBZ1AfKCBQZnnqFJ6mqMqGdiTS5uVCF1lIKBarXeD6nEmRZj9Ng94pnHw";
    private final String code = "BecAFGVdZhX-8pSEQAxL6l9B3bzZnPpac0H_FEjDLr4MJUQ90L-NuWJFXs0_gIp6h74ugwo9c5oAAAGJiTbIcg";

    @Override
    protected Object initController() {
        return new UserController(userService);
    }

    @DisplayName("소셜 로그인 API")
    @Test
    void loginByOAuth() throws Exception {
        // given

        given(userService.loginByOAuth("Hx-PXmWuFaGakYCEy8hkUIVOWUSXIOtD7cosKDSIKsiwodR1g35KXQQWX9H4hXlcpZ45eSgo3dGkWWWOSX-z9iQ", NAVER))
                .willReturn(
                        UserResponse.Login.builder()
                                .email("moidots@gmail.com")
                                .name("모이닷")
                                .token(token)
                                .build()
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/signin")
                                .param("code", "Hx-PXmWuFaGakYCEy8hkUIVOWUSXIOtD7cosKDSIKsiwodR1g35KXQQWX9H4hXlcpZ45eSgo3dGkWWWOSX-z9iQ")
                                .param("platform", "NAVER")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("social-login",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("code").description("소셜 로그인 redirect 인가 코드"),
                                parameterWithName("platform").description("플랫폼 : 'NAVER' / 'KAKAO' / 'GOOGLE' ")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("상태 코드"),
                                fieldWithPath("message").type(STRING).description("상태 메세지"),
                                fieldWithPath("data.email").type(STRING).description("유저 이메일"),
                                fieldWithPath("data.name").type(STRING).description("유저 이름"),
                                fieldWithPath("data.token").type(STRING).description("발급된 JWT 토큰")
                        )
                ));
    }
}
