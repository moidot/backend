package com.moim.backend.docs.user;

import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.user.controller.UserController;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.request.UserRequest;
import com.moim.backend.domain.user.response.UserResponse;
import com.moim.backend.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);
    private final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5dS1qdW5nMzE0NzZAbmF2ZXIuY29tIiwiZXhwIjoxNjg5MjYwODM2fQ.cgZ8eFDU_Gz7Z3EghXxoa3v-iXUeQmBZ1AfKCBQZnnqFJ6mqMqGdiTS5uVCF1lIKBarXeD6nEmRZj9Ng94pnHw";
    private final String authorizationCode = "BecAFGVdZhX-8pSEQAxL6l9B3bzZnPpac0H_FEjDLr4MJUQ90L-NuWJFXs0_gIp6h74ugwo9c5oAAAGJiTbIcg";

    @Override
    protected Object initController() {
        return new UserController(userService);
    }

    @DisplayName("이메일로 로그인하는 API")
    @Test
    void loginByEmail() throws Exception {
        // given
        UserRequest.Login request = new UserRequest.Login("yujung-31476@naver.com", "김유정");

        given(userService.login(any()))
                .willReturn(UserResponse.Login.builder()
                        .email("test@gmail.com")
                        .name("테스터")
                        .token(token)
                        .build());

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("login-by-email",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("이름")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING)
                                        .description("유저 이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data.token").type(JsonFieldType.STRING)
                                        .description("엑세스 토큰(시간, 이메일, 이름에 따라 달라짐)")
                        ))
                );
    }

    @DisplayName("토큰으로 유저 이름 가져오는 API")
    @Test
    void getUserNameByToken() throws Exception {
        // given
        given(userService.getUserNameByToken(any()))
                .willReturn("테스터");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/user")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-user-name-by-token",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data").type(JsonFieldType.STRING)
                                        .description("이름")
                        ))
                );
    }

    @DisplayName("카카오 로그인")
    @Test
    void kakaoByLogin() throws Exception {
        // given
        given(userService.loginByKakao(any()))
                .willReturn(UserResponse.Login.builder()
                        .email("test@gmail.com")
                        .name("테스터")
                        .token(token)
                        .build());

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/user/login/kakao")
                                .param("authorizationCode", authorizationCode)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("login-by-kakao",
                        preprocessResponse(prettyPrint()),
                        formParameters(
                                parameterWithName("authorizationCode")
                                        .description("카카오 인가 코드")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("상태 메세지"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING)
                                        .description("유저 이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data.token").type(JsonFieldType.STRING)
                                        .description("엑세스 토큰(시간, 이메일, 이름에 따라 달라짐)")
                        ))
                );
    }

}
