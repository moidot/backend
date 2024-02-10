package com.moim.backend.docs.user;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.user.controller.AuthController;
import com.moim.backend.domain.user.response.UserLoginResponse;
import com.moim.backend.domain.user.response.UserReissueResponse;
import com.moim.backend.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.moim.backend.domain.user.config.Platform.NAVER;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);
    private final String code = "Hx-PXmWuFaGakYCEy8hkUIVOWUSXIOtD7cosKDSIKsiwodR1g35KXQQWX9H4hXlcpZ45eSgo3dGkWWWOSX-z9iQ";
    private final String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5dS1qdW5nMzE0NzZAbmF2ZXIuY29tIiwiZXhwIjoxNjg5MjYwODM2fQ.cgZ8eFDU_Gz7Z3EghXxoa3v-iXUeQmBZ1AfKCBQZnnqFJ6mqMqGdiTS5uVCF1lIKBarXeD6nEmRZj9Ng94pnHw";
    private final String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5dS1qdW5nMzE0NzZAbmF2ZXIuY29tIiwiZXhwIjoxNjg5MjYwODM2fQ.cgZ8eFDU_Gz7Z3EghXxoa3v-iXUeQmBZ1AfKCBQZnnqFJ6mqMqGdiTS5uVCF1lIKBarXeD6nEmRZj9Ng94pnHw";

    @Override
    protected Object initController() {
        return new AuthController(userService);
    }

    @DisplayName("소셜 로그인 API")
    @Test
    void loginByOAuth() throws Exception {
        // given
        given(userService.loginByOAuth(code, NAVER))
                .willReturn(
                        UserLoginResponse.builder()
                                .userId(1L)
                                .email("moidots@gmail.com")
                                .name("모이닷")
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build()
                );

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/auth/signin")
                .param("code", "Hx-PXmWuFaGakYCEy8hkUIVOWUSXIOtD7cosKDSIKsiwodR1g35KXQQWX9H4hXlcpZ45eSgo3dGkWWWOSX-z9iQ")
                .param("platform", "NAVER");

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("유저 API")
                .summary("소셜 로그인 API")
                .queryParameters(
                        parameterWithName("code").description("소셜 로그인 redirect 인가 코드"),
                        parameterWithName("platform").description("플랫폼 : 'NAVER' / 'KAKAO' / 'GOOGLE' "))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.userId").type(NUMBER).description("유저 아이디"),
                        fieldWithPath("data.email").type(STRING).description("유저 이메일"),
                        fieldWithPath("data.name").type(STRING).description("유저 이름"),
                        fieldWithPath("data.accessToken").type(STRING).description("엑세스 토큰"),
                        fieldWithPath("data.refreshToken").type(STRING).description("리프레쉬 토큰"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("social-login", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("소셜 로그인 API")
    @Test
    void socialLoginByAccessToken() throws Exception {
        // given
        given(userService.loginByAccessToken(accessToken, NAVER))
                .willReturn(
                        UserLoginResponse.builder()
                                .userId(1L)
                                .email("moidots@gmail.com")
                                .name("모이닷")
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build()
                );

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/auth/signin/token")
                .param("token", accessToken)
                .param("platform", "NAVER");

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("유저 API")
                .summary("소셜 로그인 API")
                .queryParameters(
                        parameterWithName("token").description("소셜 로그인 redirect 엑세스 코드"),
                        parameterWithName("platform").description("플랫폼 : 'NAVER' / 'KAKAO' / 'GOOGLE' "))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.userId").type(NUMBER).description("유저 아이디"),
                        fieldWithPath("data.email").type(STRING).description("유저 이메일"),
                        fieldWithPath("data.name").type(STRING).description("유저 이름"),
                        fieldWithPath("data.accessToken").type(STRING).description("엑세스 토큰"),
                        fieldWithPath("data.refreshToken").type(STRING).description("리프레쉬 토큰"))
                .build();

        RestDocumentationResultHandler document =
                documentHandler("social-login-by-access-token", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("엑세스 토큰 재발급")
    @Test
    void reissueAccessToken() throws Exception {
        // given
        given(userService.reissueAccessToken(accessToken))
                .willReturn(UserReissueResponse.toResponse(accessToken));

        MockHttpServletRequestBuilder httpRequest = RestDocumentationRequestBuilders.get("/auth/refresh")
                .header("refreshToken", refreshToken);

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("유저 API")
                .summary("엑세스토큰 재발급 API")
                .requestHeaders(
                        headerWithName("refreshToken")
                                .description("로그인 후 리프레쉬 토큰")
                )
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"),
                        fieldWithPath("data.accessToken").type(STRING).description("엑세스 토큰")
                )
                .build();

        RestDocumentationResultHandler document =
                documentHandler("reissue-access-token", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(httpRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("로그아웃 API")
    @Test
    void logout() throws Exception {
        // given
        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("유저 API")
                .summary("로그아웃 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"))
                .build();

        RestDocumentationResultHandler document = documentHandler("logout", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/auth/logout")
                                .header(AUTHORIZATION, "Bearer {token}")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("회원탈퇴 API")
    @Test
    void deleteAccount() throws Exception {
        // given
        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("유저 API")
                .summary("회원탈퇴 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메세지"))
                .build();

        RestDocumentationResultHandler document = documentHandler("delete-account", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/auth")
                                .header(AUTHORIZATION, "Bearer {token}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }
}
